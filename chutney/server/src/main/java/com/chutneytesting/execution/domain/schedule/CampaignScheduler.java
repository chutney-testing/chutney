/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.domain.schedule;

import com.chutneytesting.campaign.domain.Frequency;
import com.chutneytesting.campaign.domain.PeriodicScheduledCampaign;
import com.chutneytesting.campaign.domain.PeriodicScheduledCampaign.CampaignExecutionRequest;
import com.chutneytesting.campaign.domain.ScheduledCampaignRepository;
import com.chutneytesting.execution.domain.campaign.CampaignExecutionEngine;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class CampaignScheduler {

    public static final String SCHEDULER_EXECUTE_USER = "auto";
    private static final Logger LOGGER = LoggerFactory.getLogger(CampaignScheduler.class);

    private final CampaignExecutionEngine campaignExecutionEngine;
    private final ScheduledCampaignRepository scheduledCampaignRepository;
    private final Clock clock;
    private final ExecutorService executor;

    public CampaignScheduler(
        CampaignExecutionEngine campaignExecutionEngine,
        Clock clock,
        ScheduledCampaignRepository scheduledCampaignRepository,
        @Qualifier("scheduledCampaignsExecutor") ExecutorService executor
    ) {
        this.campaignExecutionEngine = campaignExecutionEngine;
        this.clock = clock;
        this.scheduledCampaignRepository = scheduledCampaignRepository;
        this.executor = executor;
    }

    @Async("scheduleCampaignsExecutor")
    public void executeScheduledCampaigns() {
        try {
            executor.invokeAll(
                scheduledCampaignsToExecute()
                    .map(this::executeScheduledCampaign)
                    .collect(Collectors.toList())
            );
        } catch (InterruptedException e) {
            LOGGER.error("Scheduled campaigns thread interrupted", e);
        }
    }

    private Callable<Void> executeScheduledCampaign(Pair<List<CampaignExecutionRequest>, String> executionRequests) {
        LOGGER.info(executionRequests.toString());
        String environment = executionRequests.getRight();
        return () -> {
            executionRequests.getLeft().forEach(executionRequest -> {
                try {
                    LOGGER.info("Execute campaign with id [{}]", executionRequest);
                    campaignExecutionEngine.executeScheduledCampaign(executionRequest.campaignId(), environment, executionRequest.datasetId(), SCHEDULER_EXECUTE_USER);
                } catch (Exception e) {
                    LOGGER.error("Error during campaign [{}] execution", executionRequest, e);
                }
            });
            return null;
        };
    }

    synchronized private Stream<Pair<List<CampaignExecutionRequest>, String>> scheduledCampaignsToExecute() {
        try {
            List<PeriodicScheduledCampaign> all = scheduledCampaignRepository.getAll();
            LOGGER.info(all.toString());
            return all.stream()
                .filter(sc -> sc.nextExecutionDate != null)
                .filter(sc -> sc.nextExecutionDate.isBefore(LocalDateTime.now(clock)))
                .peek(this::prepareScheduledCampaignForNextExecution)
                .map(sc -> Pair.of(sc.campaignExecutionRequests, sc.environment));
        } catch (Exception e) {
            LOGGER.error("Error retrieving scheduled campaigns", e);
            return Stream.empty();
        }
    }

    private void prepareScheduledCampaignForNextExecution(PeriodicScheduledCampaign periodicScheduledCampaign) {
        try {
            if (!Frequency.EMPTY.equals(periodicScheduledCampaign.frequency)) {
                PeriodicScheduledCampaign periodicScheduledCampaignWithNextSchedule = periodicScheduledCampaign;
                while (periodicScheduledCampaignWithNextSchedule.nextExecutionDate.isBefore(LocalDateTime.now(clock))) {
                    periodicScheduledCampaignWithNextSchedule = periodicScheduledCampaignWithNextSchedule.nextScheduledExecution();
                }
                scheduledCampaignRepository.add(periodicScheduledCampaignWithNextSchedule);
                LOGGER.info("Next execution of scheduled campaign(s) {} with frequency [{}] has been added", periodicScheduledCampaign.campaignExecutionRequests, periodicScheduledCampaign.frequency);
            }
            scheduledCampaignRepository.removeById(periodicScheduledCampaign.id);
        } catch (Exception e) {
            LOGGER.error("Error preparing scheduled campaign next execution [{}]", periodicScheduledCampaign.id, e);
        }
    }
}

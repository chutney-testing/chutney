/*
 *  Copyright 2017-2023 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.chutneytesting.execution.domain.purge;

import static com.chutneytesting.execution.domain.purge.PurgeExecutionsFilters.isExecutionDateBeforeNowMinusOffset;
import static com.chutneytesting.execution.domain.purge.PurgeExecutionsFilters.isScenarioExecutionLinkedWithCampaignExecution;
import static com.chutneytesting.server.core.domain.execution.report.ServerReportStatus.SUCCESS;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Comparator.comparing;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;

import com.chutneytesting.campaign.domain.CampaignExecutionRepository;
import com.chutneytesting.campaign.domain.CampaignRepository;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory.ExecutionSummary;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistoryRepository;
import com.chutneytesting.server.core.domain.execution.history.PurgeService;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import com.chutneytesting.server.core.domain.scenario.TestCaseMetadata;
import com.chutneytesting.server.core.domain.scenario.TestCaseRepository;
import com.chutneytesting.server.core.domain.scenario.campaign.Campaign;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PurgeServiceImpl implements PurgeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PurgeServiceImpl.class);
    public static final int ONE_DAY_MILLIS = Long.valueOf(Duration.ofDays(1).toMillis()).intValue();
    private final PurgeExecutionService<Campaign, Long, CampaignExecution> campaignPurgeService;
    private final PurgeExecutionService<TestCaseMetadata, String, ExecutionSummary> scenarioPurgeService;

    PurgeServiceImpl(
        TestCaseRepository testCaseRepository,
        ExecutionHistoryRepository executionsRepository,
        CampaignRepository campaignRepository,
        CampaignExecutionRepository campaignExecutionRepository,
        int maxScenarioExecutionsConfiguration,
        int maxCampaignExecutionsConfiguration
    ) {
        this(testCaseRepository,
            executionsRepository,
            campaignRepository,
            campaignExecutionRepository,
            maxScenarioExecutionsConfiguration,
            0,
            maxCampaignExecutionsConfiguration,
            0);
    }

    public PurgeServiceImpl(
        TestCaseRepository testCaseRepository,
        ExecutionHistoryRepository executionsRepository,
        CampaignRepository campaignRepository,
        CampaignExecutionRepository campaignExecutionRepository,
        int maxScenarioExecutionsConfiguration,
        int beforeNowMinusOffsetScenarioExecutionsConfiguration,
        int maxCampaignExecutionsConfiguration,
        int beforeNowMinusOffsetCampaignExecutionsConfiguration
    ) {
        int maxScenarioExecutions = checkPositiveOrDefault(maxScenarioExecutionsConfiguration, "maxScenarioExecutions", 10);
        int maxCampaignExecutions = checkPositiveOrDefault(maxCampaignExecutionsConfiguration, "maxCampaignExecutions", 10);
        int scenarioBeforeHoursTimeExecutions = checkPositiveOrDefault(beforeNowMinusOffsetScenarioExecutionsConfiguration, "beforeNowMinusOffsetScenarioExecutions", ONE_DAY_MILLIS);
        int campaignsBeforeHoursTimeExecutions = checkPositiveOrDefault(beforeNowMinusOffsetCampaignExecutionsConfiguration, "beforeNowMinusOffsetCampaignExecutions", ONE_DAY_MILLIS);

        this.scenarioPurgeService = buildScenarioService(testCaseRepository, executionsRepository, maxScenarioExecutions, scenarioBeforeHoursTimeExecutions);
        this.campaignPurgeService = buildCampaignService(campaignRepository, campaignExecutionRepository, maxCampaignExecutions, campaignsBeforeHoursTimeExecutions);
    }

    private static int checkPositiveOrDefault(
        int configurationLimit,
        String configName,
        int defaultValue
    ) {
        if (configurationLimit < 0) {
            LOGGER.warn("Purge configuration limit must be positive. Defaulting {} to {}", configName, defaultValue);
            return defaultValue;
        }
        return configurationLimit;
    }

    private static PurgeExecutionService<TestCaseMetadata, String, ExecutionSummary> buildScenarioService(
        TestCaseRepository testCaseRepository,
        ExecutionHistoryRepository executionsRepository,
        int maxScenarioExecutions,
        int beforeHoursTimeExecutions
    ) {
        return new PurgeExecutionService<>(
            maxScenarioExecutions,
            testCaseRepository::findAll,
            TestCaseMetadata::id,
            executionsRepository::getExecutions,
            isScenarioExecutionLinkedWithCampaignExecution.and(isExecutionDateBeforeNowMinusOffset(ExecutionSummary::time, beforeHoursTimeExecutions)),
            ExecutionSummary::executionId,
            ExecutionSummary::time,
            ExecutionSummary::status,
            ExecutionSummary::environment,
            executionsRepository::deleteExecutions
        );
    }

    private PurgeExecutionService<Campaign, Long, CampaignExecution> buildCampaignService(
        CampaignRepository campaignRepository,
        CampaignExecutionRepository campaignExecutionRepository,
        int maxCampaignExecutions,
        int beforeHoursTimeExecutions
    ) {
        return new PurgeExecutionService<>(
            maxCampaignExecutions,
            campaignRepository::findAll,
            campaign -> campaign.id,
            campaignExecutionRepository::getExecutionHistory,
            isExecutionDateBeforeNowMinusOffset(cer -> cer.startDate, beforeHoursTimeExecutions),
            cer -> cer.executionId,
            cer -> cer.startDate,
            CampaignExecution::status,
            cer -> cer.executionEnvironment,
            campaignExecutionRepository::deleteExecutions
        ) {
            // Not thread safe
            private Map<Boolean, List<CampaignExecution>> campaignExecutionsByPartialExecution;
            private List<CampaignExecution> emptyCampaignExecutions;

            /**
             * Transform executions to filter only those that are not manual replays and not empty.
             */
            @Override
            public List<CampaignExecution> handleExecutionsForOneEnvironment(List<CampaignExecution> executionsFromOneEnvironment) {
                Map<Boolean, List<CampaignExecution>> campaignExecutionsByEmpty = executionsFromOneEnvironment.stream()
                    .collect(groupingBy(cer -> cer.scenarioExecutionReports().isEmpty()));
                emptyCampaignExecutions = ofNullable(campaignExecutionsByEmpty.get(true)).orElse(emptyList());

                campaignExecutionsByPartialExecution = ofNullable(campaignExecutionsByEmpty.get(false)).orElse(emptyList()).stream()
                    .collect(groupingBy(cer -> cer.partialExecution));
                return ofNullable(campaignExecutionsByPartialExecution.get(false)).orElse(emptyList());
            }

            /**
             * Select all manual replays (for deletion) that are older than the oldest campaign execution kept and empty executions.
             */
            @Override
            public Collection<Long> findExtraExecutionsIdsToDelete(List<CampaignExecution> timeSortedExecutionsForOneEnvironment) {
                return Stream.concat(
                    manualReplaysOlderThanOldestCampaignExecution(timeSortedExecutionsForOneEnvironment),
                    emptyCampaignExecutions.stream().map(ce -> ce.executionId)
                ).toList();
            }

            private Stream<Long> manualReplaysOlderThanOldestCampaignExecution(List<CampaignExecution> timeSortedExecutionsForOneEnvironment) {
                LocalDateTime oldestCampaignExecutionToKeptStartDate;
                if (!timeSortedExecutionsForOneEnvironment.isEmpty() && maxCampaignExecutions > 0 && maxCampaignExecutions < timeSortedExecutionsForOneEnvironment.size()) {
                    oldestCampaignExecutionToKeptStartDate = timeSortedExecutionsForOneEnvironment.get(maxCampaignExecutions - 1).startDate;
                } else {
                    oldestCampaignExecutionToKeptStartDate = LocalDateTime.MAX;
                }

                return ofNullable(campaignExecutionsByPartialExecution.get(true)).orElse(emptyList()).stream()
                    .filter(cer -> cer.startDate.isBefore(oldestCampaignExecutionToKeptStartDate))
                    .map(cer -> cer.executionId);
            }
        };
    }

    @Override
    public PurgeReport purge() {
        Set<Long> purgedCampaignsExecutionsIds = campaignPurgeService.purgeExecutions();
        Set<Long> purgedScenariosExecutionsIds = scenarioPurgeService.purgeExecutions();
        LOGGER.info("Purge report : {} scenarios' executions deleted - {} campaigns' executions deleted", purgedScenariosExecutionsIds.size(), purgedCampaignsExecutionsIds.size());
        return new PurgeReport(purgedScenariosExecutionsIds, purgedCampaignsExecutionsIds);
    }

    /**
     * Core logic to purge executions.
     *
     * @see #purgeExecutions()
     */
    private static class PurgeExecutionService<Base, BaseId, Execution> {
        /**
         * The configuration defining the number of executions to keep
         */
        private final int maxExecutionsToKeep;
        /**
         * A supplier of a domain object related to executions.
         *
         * @see TestCaseMetadata
         * @see Campaign
         */
        private final Supplier<List<Base>> baseObject;
        /**
         * A mapper function extracting the domain object id
         */
        private final Function<Base, BaseId> idFunction;
        /**
         * A function returning all executions given an ObjectTypeId
         */
        private final Function<BaseId, List<Execution>> executionsFunction;
        /**
         * An optional executions filter used in {@link #purgeExecutions()} before grouping by environment
         */
        private final Predicate<Execution> executionsFilter;
        /**
         * A mapper function extracting the execution id
         */
        private final Function<Execution, Long> executionIdFunction;
        /**
         * A mapper function extracting the execution date for sorting
         */
        private final Function<Execution, LocalDateTime> executionDateFunction;
        /**
         * A mapper function extracting the execution status for keeping the last success one
         */
        private final Function<Execution, ServerReportStatus> statusFunction;
        /**
         * A mapper function extracting the execution environment
         */
        private final Function<Execution, String> environmentFunction;
        /**
         * A function deleting executions by ids
         */
        private final Consumer<Set<Long>> deleteFunction;

        private PurgeExecutionService(
            int maxExecutionsToKeep,
            Supplier<List<Base>> baseObjectSupplier,
            Function<Base, BaseId> idFunction,
            Function<BaseId, List<Execution>> executionsFunction,
            Predicate<Execution> executionsFilter,
            Function<Execution, Long> executionIdFunction,
            Function<Execution, LocalDateTime> executionDateFunction,
            Function<Execution, ServerReportStatus> statusFunction,
            Function<Execution, String> environmentFunction,
            Consumer<Set<Long>> deleteFunction
        ) {
            this.maxExecutionsToKeep = maxExecutionsToKeep;
            this.baseObject = baseObjectSupplier;
            this.idFunction = idFunction;
            this.executionsFunction = executionsFunction;
            this.executionsFilter = executionsFilter;
            this.executionIdFunction = executionIdFunction;
            this.executionDateFunction = executionDateFunction;
            this.statusFunction = statusFunction;
            this.environmentFunction = environmentFunction;
            this.deleteFunction = deleteFunction;
        }

        /**
         * Purge executions.
         * <p> Find all base objects ids and map them to executions.</p>
         * <p> Keep only executions before last 24 hours.</p>
         * <p>For each group of executions, filter then group them by environment.</p>
         * <p>For each group of executions by environment,</p>
         * <p>permits a specific handle via {@link #handleExecutionsForOneEnvironment(List)}</p>
         * <p>then call {@link #purgeOldestExecutionsFromOneEnvironment(List)}</p>
         *
         * @return The list of deleted executions ids.
         */
        Set<Long> purgeExecutions() {
            Set<Long> deletedExecutionsIds = new HashSet<>();
            baseObject.get().stream()
                .map(idFunction)
                .map(executionsFunction)
                .forEach(executionsReports -> {
                    var executionsByEnvironment = executionsReports.stream()
                        .filter(executionsFilter)
                        .collect(groupingBy(t -> {
                            var env = environmentFunction.apply(t);
                            return env != null ? env : "";
                        }));
                    for (List<Execution> executionsOneEnvironment : executionsByEnvironment.values()) {
                        purgeOneBaseObjectExecutionsForOneEnvironment(executionsOneEnvironment, deletedExecutionsIds);
                    }
                });
            return deletedExecutionsIds;
        }

        private void purgeOneBaseObjectExecutionsForOneEnvironment(List<Execution> executionsOneEnvironment, Set<Long> deletedExecutionsIds) {
            try {
                List<Execution> executionsToDelete = handleExecutionsForOneEnvironment(executionsOneEnvironment);
                Set<Long> deleteExecutionsIds = purgeOldestExecutionsFromOneEnvironment(executionsToDelete);
                deletedExecutionsIds.addAll(deleteExecutionsIds);
            } catch (Exception e) {
                LOGGER.error("Cannot purge executions {}", executionsOneEnvironment, e);
            }
        }

        /**
         * Purge the oldest executions according to {@link #maxExecutionsToKeep} configuration
         * by keeping the last success execution no matter what. <br/>
         * Check for existence.
         * Sort by date.
         * Call {@link #findOldestExecutionsIdsWhileKeepingTheLastSuccess(List)}.
         * Permits to add other executions ids via {@link #findExtraExecutionsIdsToDelete(List)}.
         * Delete executions.
         *
         * @return The list of deleted executions ids.
         */
        private Set<Long> purgeOldestExecutionsFromOneEnvironment(List<Execution> executionsFromOneEnvironment) {
            List<Execution> timeSortedExecutionsForOneEnvironment = executionsFromOneEnvironment.stream()
                .sorted(comparing(executionDateFunction).reversed())
                .toList();

            Set<Long> deletedExecutionsIdsTmp = new HashSet<>();

            deletedExecutionsIdsTmp.addAll(
                findOldestExecutionsIdsWhileKeepingTheLastSuccess(timeSortedExecutionsForOneEnvironment)
            );

            deletedExecutionsIdsTmp.addAll(
                findExtraExecutionsIdsToDelete(timeSortedExecutionsForOneEnvironment)
            );

            if (!deletedExecutionsIdsTmp.isEmpty()) {
                deleteFunction.accept(deletedExecutionsIdsTmp);
            }
            return deletedExecutionsIdsTmp;
        }

        private Collection<Long> findOldestExecutionsIdsWhileKeepingTheLastSuccess(List<Execution> timeSortedExecutions) {
            Long youngestSuccessExecutionIdToDelete = youngestSuccessExecutionIdToDelete(timeSortedExecutions);
            return timeSortedExecutions.stream()
                .skip(maxExecutionsToKeep)
                .map(executionIdFunction)
                .filter(id -> !youngestSuccessExecutionIdToDelete.equals(id))
                .collect(toSet());
        }

        // The list parameter must be sorted from younger to oldest
        private Long youngestSuccessExecutionIdToDelete(List<Execution> timeSortedExecutions) {
            boolean isSuccessExecutionKept = timeSortedExecutions.stream()
                .limit(maxExecutionsToKeep)
                .map(statusFunction)
                .anyMatch(SUCCESS::equals);

            if (!isSuccessExecutionKept) {
                return timeSortedExecutions.stream()
                    .skip(maxExecutionsToKeep)
                    .filter(es -> SUCCESS.equals(statusFunction.apply(es)))
                    .findFirst()
                    .map(executionIdFunction)
                    .orElse(-1L);
            }
            return -1L;
        }

        /**
         * To implement in order to control the list of executions of one {@link #baseObject} for one environment
         * to be passed down to {@link #purgeOldestExecutionsFromOneEnvironment(List)}. <br/>
         * This list is not sorted.
         *
         * @return Default implementation returns the same list.
         */
        protected List<Execution> handleExecutionsForOneEnvironment(List<Execution> executionsFromOneEnvironment) {
            return executionsFromOneEnvironment;
        }

        /**
         * To implement to find extra executions' ids to delete.
         *
         * @param timeSortedExecutionsForOneEnvironment The current list of executions processed by {@link #purgeOldestExecutionsFromOneEnvironment(List)}, sorted by {@link #executionDateFunction}
         * @return Default implementation returns a {@link Collections#emptySet()}
         */
        protected Collection<Long> findExtraExecutionsIdsToDelete(List<Execution> timeSortedExecutionsForOneEnvironment) {
            return emptySet();
        }
    }
}

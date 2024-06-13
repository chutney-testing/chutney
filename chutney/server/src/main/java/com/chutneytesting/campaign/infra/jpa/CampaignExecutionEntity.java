/*
 * Copyright 2017-2023 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chutneytesting.campaign.infra.jpa;

import static java.util.Optional.ofNullable;

import com.chutneytesting.execution.infra.storage.jpa.ScenarioExecutionEntity;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import com.chutneytesting.server.core.domain.execution.history.ImmutableExecutionHistory;
import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecutionReportBuilder;
import com.chutneytesting.server.core.domain.scenario.campaign.ScenarioExecutionCampaign;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Entity(name = "CAMPAIGN_EXECUTIONS")
public class CampaignExecutionEntity {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "CAMPAIGN_ID")
    private Long campaignId;

    @OneToMany(mappedBy = "campaignExecution")
    private List<ScenarioExecutionEntity> scenarioExecutions;

    @Column(name = "PARTIAL")
    private Boolean partial;

    @Column(name = "ENVIRONMENT")
    private String environment;

    @Column(name = "USER_ID")
    private String userId;

    @Column(name = "DATASET_ID")
    private String datasetId;

    @Column(name = "VERSION")
    @Version
    private Integer version;

    public CampaignExecutionEntity() {
    }

    public CampaignExecutionEntity(Long campaignId, String environment) {
        this(null, campaignId, null, null, environment, null, null, null);
    }

    public CampaignExecutionEntity(Long id, Long campaignId, List<ScenarioExecutionEntity> scenarioExecutions, Boolean partial, String environment, String userId, String datasetId, Integer version) {
        this.id = id;
        this.campaignId = campaignId;
        this.scenarioExecutions = scenarioExecutions;
        this.partial = ofNullable(partial).orElse(false);
        this.environment = environment;
        this.userId = userId;
        this.datasetId = datasetId;
        this.version = version;
    }

    public Long id() {
        return id;
    }

    public Long campaignId() {
        return campaignId;
    }

    public List<ScenarioExecutionEntity> scenarioExecutions() {
        return scenarioExecutions;
    }

    public String environment() { return environment;}

    public void updateFromDomain(CampaignExecution report, Iterable<ScenarioExecutionEntity> scenarioExecutions) {
        //id = report.executionId;
        //campaignId = report.campaignId;
        partial = report.partialExecution;
        environment = report.executionEnvironment;
        userId = report.userId;
        datasetId = report.dataSetId;
        this.scenarioExecutions.clear();
        scenarioExecutions.forEach(se -> {
            se.forCampaignExecution(this);
            this.scenarioExecutions.add(se);
        });
    }

    public CampaignExecution toDomain(CampaignEntity campaign, boolean isRunning, Function<String, String> titleSupplier) {
        return toDomain(campaign, true, isRunning, titleSupplier);
    }

    public CampaignExecution toDomain(CampaignEntity campaign, boolean withScenariosExecutions, boolean isRunning, Function<String, String> scenarioTitleSupplier) {
        if (withScenariosExecutions) {
            if (isRunning) {
                var scenarioExecutionReports = campaign.campaignScenarios().stream()
                    .map(cs ->
                        scenarioExecutions.stream()
                            .filter(se -> se.scenarioId().equals(cs.scenarioId()) && ofNullable(se.datasetId()).equals(ofNullable(cs.datasetId())))
                            .findFirst()
                            .map(se -> new ScenarioExecutionCampaign(se.scenarioId(), se.scenarioTitle(), se.toDomain()))
                            .orElseGet(() -> blankScenarioExecutionReport(cs, scenarioTitleSupplier)))
                    .collect(Collectors.toCollection(ArrayList::new));

                return CampaignExecutionReportBuilder.builder()
                    .executionId(id)
                    .campaignId(campaignId)
                    .campaignName(campaign.title())
                    .partialExecution(ofNullable(partial).orElse(false))
                    .environment(environment)
                    .dataSetId(datasetId)
                    .userId(userId)
                    .status(ServerReportStatus.RUNNING)
                    .scenarioExecutionReport(scenarioExecutionReports)
                    .build();
            } else {
                var scenarioExecutionReports = scenarioExecutions.stream()
                    .map(se -> new ScenarioExecutionCampaign(se.scenarioId(), se.scenarioTitle(), se.toDomain()))
                    .collect(Collectors.toCollection(ArrayList::new));

                return CampaignExecutionReportBuilder.builder()
                    .executionId(id)
                    .campaignId(campaignId)
                    .campaignName(campaign.title())
                    .partialExecution(ofNullable(partial).orElse(false))
                    .environment(environment)
                    .dataSetId(datasetId)
                    .userId(userId)
                    .scenarioExecutionReport(scenarioExecutionReports)
                    .build();
            }
        }

        return CampaignExecutionReportBuilder.builder()
            .executionId(id)
            .campaignId(campaignId)
            .campaignName(campaign.title())
            .partialExecution(ofNullable(partial).orElse(false))
            .environment(environment)
            .dataSetId(datasetId)
            .userId(userId)
            .build();
    }

    private ScenarioExecutionCampaign blankScenarioExecutionReport(CampaignScenarioEntity campaignScenarioEntity, Function<String, String> titleSupplier) {
        String scenarioTitle = titleSupplier.apply(campaignScenarioEntity.scenarioId());
        return new ScenarioExecutionCampaign(campaignScenarioEntity.scenarioId(), scenarioTitle, blankScenarioExecution(scenarioTitle, campaignScenarioEntity.scenarioId()));
    }

    private ExecutionHistory.ExecutionSummary blankScenarioExecution(String testCaseTitle, String scenarioId) {
        return ImmutableExecutionHistory.ExecutionSummary.builder()
            .executionId(-1L)
            .testCaseTitle(testCaseTitle)
            .time(LocalDateTime.now())
            .status(ServerReportStatus.NOT_EXECUTED)
            .duration(0)
            .environment("")
            .user("")
            .scenarioId(scenarioId)
            .build();
    }
}

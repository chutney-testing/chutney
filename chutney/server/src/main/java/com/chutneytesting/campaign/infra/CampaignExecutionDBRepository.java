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

package com.chutneytesting.campaign.infra;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import com.chutneytesting.campaign.domain.CampaignExecutionRepository;
import com.chutneytesting.campaign.domain.CampaignNotFoundException;
import com.chutneytesting.campaign.infra.jpa.CampaignEntity;
import com.chutneytesting.campaign.infra.jpa.CampaignExecutionEntity;
import com.chutneytesting.execution.domain.campaign.CampaignExecutionNotFoundException;
import com.chutneytesting.execution.infra.storage.DatabaseExecutionJpaRepository;
import com.chutneytesting.execution.infra.storage.jpa.ScenarioExecutionEntity;
import com.chutneytesting.server.core.domain.scenario.TestCaseMetadata;
import com.chutneytesting.server.core.domain.scenario.TestCaseRepository;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class CampaignExecutionDBRepository implements CampaignExecutionRepository {

    private final CampaignExecutionJpaRepository campaignExecutionJpaRepository;
    private final CampaignJpaRepository campaignJpaRepository;
    private final DatabaseExecutionJpaRepository scenarioExecutionJpaRepository;
    private final TestCaseRepository testCaseRepository;
    private final Map<Long, List<CampaignExecution>> currentCampaignExecutions = new ConcurrentHashMap<>();

    public CampaignExecutionDBRepository(
        CampaignExecutionJpaRepository campaignExecutionJpaRepository,
        CampaignJpaRepository campaignJpaRepository,
        DatabaseExecutionJpaRepository scenarioExecutionJpaRepository,
        TestCaseRepository testCaseRepository) {
        this.campaignExecutionJpaRepository = campaignExecutionJpaRepository;
        this.campaignJpaRepository = campaignJpaRepository;
        this.scenarioExecutionJpaRepository = scenarioExecutionJpaRepository;
        this.testCaseRepository = testCaseRepository;
    }

    @Override
    public List<CampaignExecution> currentExecutions(Long campaignId) {
        return currentCampaignExecutions.getOrDefault(campaignId, emptyList());
    }

    @Override
    public void startExecution(Long campaignId, CampaignExecution campaignExecution) {
        List<CampaignExecution> campaignExecutions = new ArrayList<>();
        if (currentCampaignExecutions.containsKey(campaignId)) {
            campaignExecutions = currentCampaignExecutions.get(campaignId);
        }
        campaignExecutions.add(campaignExecution);
        currentCampaignExecutions.put(campaignId, campaignExecutions);
    }

    @Override
    public void stopExecution(Long campaignId, String environment) {
        currentCampaignExecutions.get(campaignId)
            .removeIf(exec -> exec.executionEnvironment.equals(environment));
        if (currentCampaignExecutions.get(campaignId).isEmpty()) {
            currentCampaignExecutions.remove(campaignId);
        }
    }

    @Override
    public CampaignExecution getLastExecution(Long campaignId) {
        return campaignExecutionJpaRepository
            .findFirstByCampaignIdOrderByIdDesc(campaignId)
            .map(this::toDomain)
            .orElseThrow(() -> new CampaignExecutionNotFoundException(campaignId));
    }

    @Override
    @Transactional
    public void deleteExecutions(Set<Long> executionsIds) {
        List<CampaignExecutionEntity> executions = campaignExecutionJpaRepository.findAllById(executionsIds);
        List<ScenarioExecutionEntity> scenarioExecutions = executions.stream().flatMap(cer -> cer.scenarioExecutions().stream()).toList();
        scenarioExecutions.forEach(ScenarioExecutionEntity::clearCampaignExecution);
        scenarioExecutionJpaRepository.saveAll(scenarioExecutions);
        campaignExecutionJpaRepository.deleteAllInBatch(executions);
    }

    @Override
    public List<CampaignExecution> getExecutionHistory(Long campaignId) {
        CampaignEntity campaign = campaignJpaRepository.findById(campaignId).orElseThrow(() -> new CampaignNotFoundException(campaignId));
        return campaignExecutionJpaRepository.findByCampaignIdOrderByIdDesc(campaignId).stream()
            .map(ce -> toDomainWithCampaign(campaign, ce))
            .filter(Objects::nonNull)
            .collect(Collectors.toCollection(ArrayList::new));
    }

    @Override
    @Transactional
    public void saveCampaignExecution(Long campaignId, CampaignExecution campaignExecution) {
        CampaignExecutionEntity execution = campaignExecutionJpaRepository.findById(campaignExecution.executionId).orElseThrow(
            () -> new CampaignExecutionNotFoundException(campaignId, campaignExecution.executionId)
        );
        Iterable<ScenarioExecutionEntity> scenarioExecutions =
            scenarioExecutionJpaRepository.findAllById(campaignExecution.scenarioExecutionReports().stream()
                .map(serc -> serc.execution.executionId())
                .toList());
        execution.updateFromDomain(campaignExecution, scenarioExecutions);
        campaignExecutionJpaRepository.save(execution);
    }

    @Override
    public List<CampaignExecution> getLastExecutions(Long numberOfExecution) {
        return campaignExecutionJpaRepository.findAll(
                PageRequest.of(0, numberOfExecution.intValue(), Sort.by(Sort.Direction.DESC, "id"))).stream()
            .map(this::toDomain)
            .filter(Objects::nonNull)
            .toList();
    }

    @Override
    public CampaignExecution getCampaignExecutionById(Long campaignExecId) {
        return campaignExecutionJpaRepository.findById(campaignExecId)
            .map(this::toDomain)
            .orElseThrow(() -> new CampaignExecutionNotFoundException(null, campaignExecId));
    }

    @Override
    @Transactional
    public void clearAllExecutionHistory(Long campaignId) {
        List<CampaignExecutionEntity> campaignExecutionEntities = campaignExecutionJpaRepository.findAllByCampaignId(campaignId);
        List<ScenarioExecutionEntity> scenarioExecutions = campaignExecutionEntities.stream().flatMap(ce -> ce.scenarioExecutions().stream()).toList();
        scenarioExecutions.forEach(ScenarioExecutionEntity::clearCampaignExecution);
        scenarioExecutionJpaRepository.saveAll(scenarioExecutions);
        campaignExecutionJpaRepository.deleteAllInBatch(campaignExecutionEntities);
    }

    @Override
    @Transactional
    public Long generateCampaignExecutionId(Long campaignId, String environment) {
        notNull(campaignId, "Campaign ID cannot be null");
        notBlank(environment, "Environment cannot be null or empty");

        CampaignExecutionEntity newExecution = new CampaignExecutionEntity(campaignId, environment);
        campaignExecutionJpaRepository.save(newExecution);
        return newExecution.id();
    }
    private CampaignExecution toDomain(CampaignExecutionEntity campaignExecution) {
        return toDomainWithCampaign(
            campaignJpaRepository.findById(campaignExecution.campaignId()).orElseThrow(() -> new CampaignExecutionNotFoundException(campaignExecution.campaignId(), campaignExecution.id())),
            campaignExecution);
    }

    private CampaignExecution toDomainWithCampaign(CampaignEntity campaign, CampaignExecutionEntity campaignExecutionEntity) {
        return campaignExecutionEntity.toDomain(campaign, isCampaignExecutionRunning(campaignExecutionEntity), this::title);
    }

    private String title(String scenarioId) {
        return testCaseRepository.findMetadataById(scenarioId).map(TestCaseMetadata::title).orElse("");
    }

    private boolean isCampaignExecutionRunning(CampaignExecutionEntity campaignExecutionEntity) {
        return currentExecutions(campaignExecutionEntity.campaignId())
            .stream()
            .anyMatch(exec -> exec.executionId.equals(campaignExecutionEntity.id()));
    }
}

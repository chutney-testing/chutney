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
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.Validate.notBlank;
import static org.apache.commons.lang3.Validate.notNull;

import com.chutneytesting.campaign.domain.CampaignExecutionRepository;
import com.chutneytesting.campaign.domain.CampaignNotFoundException;
import com.chutneytesting.campaign.infra.jpa.CampaignEntity;
import com.chutneytesting.campaign.infra.jpa.CampaignExecutionEntity;
import com.chutneytesting.execution.domain.campaign.CampaignExecutionNotFoundException;
import com.chutneytesting.execution.infra.storage.DatabaseExecutionJpaRepository;
import com.chutneytesting.execution.infra.storage.jpa.ScenarioExecutionEntity;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
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
    private final Map<Long, List<CampaignExecution>> currentCampaignExecutions = new ConcurrentHashMap<>();

    public CampaignExecutionDBRepository(
        CampaignExecutionJpaRepository campaignExecutionJpaRepository,
        CampaignJpaRepository campaignJpaRepository,
        DatabaseExecutionJpaRepository scenarioExecutionJpaRepository
    ) {
        this.campaignExecutionJpaRepository = campaignExecutionJpaRepository;
        this.campaignJpaRepository = campaignJpaRepository;
        this.scenarioExecutionJpaRepository = scenarioExecutionJpaRepository;
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
            .map(campaignExecution -> toDomain(campaignExecution, () -> new CampaignExecutionNotFoundException(campaignExecution.campaignId(), campaignExecution.id())))
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
        return campaignExecutionJpaRepository.findByCampaignIdOrderByIdDesc(campaignId).stream()
            .map(ce -> toDomain(ce, () -> new CampaignNotFoundException(campaignId)))
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
                .map(serc -> serc.execution().executionId())
                .toList());
        execution.updateFromDomain(campaignExecution, scenarioExecutions);
        campaignExecutionJpaRepository.save(execution);
    }

    @Override
    public List<CampaignExecution> getLastExecutions(Long numberOfExecution) {
        return campaignExecutionJpaRepository.findAll(
                PageRequest.of(0, numberOfExecution.intValue(), Sort.by(Sort.Direction.DESC, "id"))).stream()
            .map(campaignExecution -> toDomain(campaignExecution, campaignExecutionNotFoundExceptionSupplier(campaignExecution)))
            .filter(Objects::nonNull)
            .toList();
    }

    @Override
    public CampaignExecution getCampaignExecutionById(Long campaignExecId) {
        return campaignExecutionJpaRepository.findById(campaignExecId)
            .map(campaignExecution -> toDomain(campaignExecution, campaignExecutionNotFoundExceptionSupplier(campaignExecution)))
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

    private CampaignExecution toDomain(CampaignExecutionEntity campaignExecution, Supplier<? extends RuntimeException> campaignExceptionSupplier) {
        CampaignEntity campaign = campaignJpaRepository.findById(campaignExecution.campaignId()).orElseThrow(campaignExceptionSupplier);
        return ofNullable(runningCampaignExecution(campaignExecution)).orElseGet(() ->
            campaignExecution.toDomain(campaign.title())
        );
    }

    private Supplier<CampaignExecutionNotFoundException> campaignExecutionNotFoundExceptionSupplier(CampaignExecutionEntity campaignExecution) {
        return () -> new CampaignExecutionNotFoundException(campaignExecution.campaignId(), campaignExecution.id());
    }

    private CampaignExecution runningCampaignExecution(CampaignExecutionEntity campaignExecutionEntity) {
        return currentExecutions(campaignExecutionEntity.campaignId())
            .stream()
            .filter(exec -> exec.executionId.equals(campaignExecutionEntity.id()))
            .findAny()
            .orElse(null);
    }
}

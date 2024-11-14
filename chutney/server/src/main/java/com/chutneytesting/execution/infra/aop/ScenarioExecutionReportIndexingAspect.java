/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.infra.aop;

import com.chutneytesting.execution.infra.storage.DatabaseExecutionJpaRepository;
import com.chutneytesting.execution.infra.storage.jpa.ScenarioExecutionEntity;
import com.chutneytesting.execution.infra.storage.jpa.ScenarioExecutionReportEntity;
import com.chutneytesting.index.infra.ScenarioExecutionReportIndexRepository;
import com.chutneytesting.scenario.infra.jpa.ScenarioEntity;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ScenarioExecutionReportIndexingAspect {
    private final ScenarioExecutionReportIndexRepository reportIndexRepository;
    private final DatabaseExecutionJpaRepository scenarioExecutionRepository;

    public ScenarioExecutionReportIndexingAspect(ScenarioExecutionReportIndexRepository reportIndexRepository, DatabaseExecutionJpaRepository scenarioExecutionRepository) {
        this.reportIndexRepository = reportIndexRepository;
        this.scenarioExecutionRepository = scenarioExecutionRepository;
    }

    @After("execution(* com.chutneytesting.execution.infra.storage.ScenarioExecutionReportJpaRepository.save(..)) && args(reportEntity)")
    public void index(ScenarioExecutionReportEntity reportEntity) {
        if (reportEntity.status().isFinal()){
            reportIndexRepository.save(reportEntity);
        }
    }

    @After("execution(* com.chutneytesting.scenario.infra.raw.ScenarioJpaRepository.save(..)) && args(scenario)")
    public void deleteDeactivatedScenarioExecutions(ScenarioEntity scenario) {
        if (!scenario.isActivated()){
            List<ScenarioExecutionEntity> executions = scenarioExecutionRepository.findAllByScenarioId(String.valueOf(scenario.getId()));
            reportIndexRepository.deleteAllById(executions.stream().map(ScenarioExecutionEntity::getId).collect(Collectors.toSet()));
        }

    }

    @After("execution(* com.chutneytesting.execution.infra.storage.ScenarioExecutionReportJpaRepository.deleteAllById(..)) && args(scenarioExecutionIds)")
    public void deleteById(Set<Long> scenarioExecutionIds) {
        reportIndexRepository.deleteAllById(scenarioExecutionIds);
    }
}

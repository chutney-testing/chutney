/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.infra.aop;

import com.chutneytesting.execution.infra.storage.jpa.ScenarioExecutionReportEntity;
import com.chutneytesting.index.infra.ScenarioExecutionReportIndexRepository;
import java.util.List;
import java.util.Set;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class IndexingAspect {
    private final ScenarioExecutionReportIndexRepository indexRepository;

    public IndexingAspect(ScenarioExecutionReportIndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    @After("execution(* com.chutneytesting.execution.infra.storage.ScenarioExecutionReportJpaRepository.save(..)) && args(reportEntity)")
    public void index(ScenarioExecutionReportEntity reportEntity) {
        indexRepository.save(reportEntity);
    }

    @After("execution(* com.chutneytesting.execution.infra.storage.ScenarioExecutionReportJpaRepository.saveAll(..)) && args(reportEntities)")
    public void indexAll(List<ScenarioExecutionReportEntity> reportEntities) {
        indexRepository.saveAll(reportEntities);
    }

    @After("execution(* com.chutneytesting.execution.infra.storage.ScenarioExecutionReportJpaRepository.delete(..)) && args(reportEntity)")
    public void delete(ScenarioExecutionReportEntity reportEntity) {
        indexRepository.delete(reportEntity.scenarioExecutionId());
    }

    @After("execution(* com.chutneytesting.execution.infra.storage.ScenarioExecutionReportJpaRepository.deleteAllById(..)) && args(scenarioExecutionIds)")
    public void deleteAllById(Set<Long> scenarioExecutionIds) {
        indexRepository.deleteAllById(scenarioExecutionIds);
    }
}

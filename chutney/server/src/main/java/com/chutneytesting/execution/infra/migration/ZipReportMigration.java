/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.infra.migration;

import static com.chutneytesting.index.infra.ScenarioExecutionReportIndexRepository.SCENARIO_EXECUTION_REPORT;
import static com.chutneytesting.index.infra.ScenarioExecutionReportIndexRepository.WHAT;

import com.chutneytesting.execution.infra.storage.ScenarioExecutionReportJpaRepository;
import com.chutneytesting.execution.infra.storage.jpa.ScenarioExecutionReportEntity;
import com.chutneytesting.index.infra.IndexRepository;
import com.chutneytesting.index.infra.ScenarioExecutionReportIndexRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ZipReportMigration implements CommandLineRunner {


    private final ScenarioExecutionReportIndexRepository scenarioExecutionReportIndexRepository;
    private final ScenarioExecutionReportJpaRepository scenarioExecutionReportJpaRepository;
    private final IndexRepository indexRepository;
    private final EntityManager entityManager;
    private static final Logger LOGGER = LoggerFactory.getLogger(ZipReportMigration.class);


    public ZipReportMigration(ScenarioExecutionReportIndexRepository scenarioExecutionReportIndexRepository, ScenarioExecutionReportJpaRepository scenarioExecutionReportJpaRepository, IndexRepository indexRepository, EntityManager entityManager) {
        this.scenarioExecutionReportIndexRepository = scenarioExecutionReportIndexRepository;
        this.scenarioExecutionReportJpaRepository = scenarioExecutionReportJpaRepository;
        this.indexRepository = indexRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (isMigrationDone()) {
            LOGGER.info("Report compression already done, skipping...");
            return;
        }
        List<ScenarioExecutionReportEntity> reportsInDb = scenarioExecutionReportJpaRepository.findAll();
        compressAndSaveInDb(reportsInDb);
        index(reportsInDb);
        LOGGER.info("{} report(s) successfully compressed and indexed", reportsInDb.size());
    }

    private void index(List<ScenarioExecutionReportEntity> reportsInDb) {
        LOGGER.info("{} report(s) will be indexed", reportsInDb.size());
        scenarioExecutionReportIndexRepository.saveAll(reportsInDb);
    }

    private void compressAndSaveInDb(List<ScenarioExecutionReportEntity> reportsInDb) {
        LOGGER.info("{} report(s) will be compressed", reportsInDb.size());

        // calling scenarioExecutionReportJpaRepository save/saveAll doesn't call ReportConverter
        // ReportConverter will be called by entityManager update. So compression will be done
        reportsInDb.forEach(report -> {
            entityManager.createQuery(
                    "UPDATE SCENARIO_EXECUTIONS_REPORTS SET report = :report WHERE id = :id")
                .setParameter("report", report.getReport())
                .setParameter("id", report.scenarioExecutionId())
                .executeUpdate();
        });
    }

    private boolean isMigrationDone() {
        Query whatQuery = new TermQuery(new Term(WHAT, SCENARIO_EXECUTION_REPORT));
        int indexedReports = indexRepository.count(whatQuery);
        return indexedReports > 0;
    }
}

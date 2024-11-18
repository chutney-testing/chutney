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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
            LOGGER.info("Report compression & indexing already done, skipping...");
            return;
        }
        PageRequest firstPage = PageRequest.of(0, 10);
        int count = 0;
        compressAndIndex(firstPage, count);
    }

    private void compressAndIndex(Pageable pageable, int previousCount) {
        Slice<ScenarioExecutionReportEntity> slice = scenarioExecutionReportJpaRepository.findAll(pageable);
        List<ScenarioExecutionReportEntity> reports = slice.getContent();

        compressAndSaveInDb(reports);
        index(reports);

        int count = previousCount + slice.getNumberOfElements();
        if (slice.hasNext()) {
            compressAndIndex(slice.nextPageable(), count);
        } else {
            LOGGER.info("{} report(s) successfully compressed and indexed", count);
        }
    }

    private void compressAndSaveInDb(List<ScenarioExecutionReportEntity> reportsInDb) {
        // calling scenarioExecutionReportJpaRepository find() and then save() doesn't call ReportConverter
        // ReportConverter will be called by entityManager update. So compression will be done
        reportsInDb.forEach(report -> {
            entityManager.createQuery(
                    "UPDATE SCENARIO_EXECUTIONS_REPORTS SET report = :report WHERE id = :id")
                .setParameter("report", report.getReport())
                .setParameter("id", report.scenarioExecutionId())
                .executeUpdate();
        });
    }

    private void index(List<ScenarioExecutionReportEntity> reportsInDb) {
        scenarioExecutionReportIndexRepository.saveAll(reportsInDb);
    }

    private boolean isMigrationDone() {
        Query whatQuery = new TermQuery(new Term(WHAT, SCENARIO_EXECUTION_REPORT));
        int indexedReports = indexRepository.count(whatQuery);
        return indexedReports > 0;
    }
}

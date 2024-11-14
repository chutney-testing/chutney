/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.index.infra;

import static org.apache.lucene.document.Field.Store;

import com.chutneytesting.execution.infra.storage.jpa.ScenarioExecutionReportEntity;
import java.util.List;
import java.util.Set;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.apache.lucene.util.BytesRef;
import org.springframework.stereotype.Repository;

@Repository
public class ScenarioExecutionReportIndexRepository {

    public static final String SCENARIO_EXECUTION_REPORT = "scenario_execution_report";
    public static final String WHAT = "what";
    private static final String SCENARIO_EXECUTION_ID = "scenarioExecutionId";
    private static final String REPORT = "report";
    private final IndexRepository indexRepository;

    public ScenarioExecutionReportIndexRepository(IndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    public void save(ScenarioExecutionReportEntity report) {
        Document document = new Document();
        document.add(new StringField(WHAT, SCENARIO_EXECUTION_REPORT, Store.NO));
        document.add(new StringField(SCENARIO_EXECUTION_ID, report.scenarioExecutionId().toString(),Store.YES));
        document.add(new TextField(REPORT, report.getReport().toLowerCase(), Store.NO));
        // for sorting
        document.add(new SortedDocValuesField(SCENARIO_EXECUTION_ID, new BytesRef(report.scenarioExecutionId().toString().getBytes()) ));


        indexRepository.index(document);
    }

    public void saveAll(List<ScenarioExecutionReportEntity> reports) {
        reports.forEach(this::save);
    }

    public void delete(Long scenarioExecutionId) {
        Query whatQuery = new TermQuery(new Term(WHAT, SCENARIO_EXECUTION_REPORT));
        Query idQuery = new TermQuery(new Term(SCENARIO_EXECUTION_ID, scenarioExecutionId.toString()));
        BooleanQuery query = new BooleanQuery.Builder()
            .add(idQuery, BooleanClause.Occur.MUST)
            .add(whatQuery, BooleanClause.Occur.MUST)
            .build();
        indexRepository.delete(query);
    }

    public void deleteAllById(Set<Long> scenarioExecutionIds) {
        scenarioExecutionIds.forEach(this::delete);
    }


    public List<Long> idsByKeywordInReport(String keyword) {
        Query whatQuery = new TermQuery(new Term(WHAT, SCENARIO_EXECUTION_REPORT));
        Query reportQuery = new WildcardQuery(new Term(REPORT,  "*" + keyword.toLowerCase() + "*"));

        BooleanQuery query = new BooleanQuery.Builder()
            .add(reportQuery, BooleanClause.Occur.MUST)
            .add(whatQuery, BooleanClause.Occur.MUST)
            .build();

        Sort sort = new Sort(SortField.FIELD_SCORE, new SortField(SCENARIO_EXECUTION_ID, SortField.Type.STRING, true));

        return indexRepository.search(query, 100, sort)
            .stream()
            .map(doc -> doc.get(SCENARIO_EXECUTION_ID))
            .map(Long::parseLong)
            .toList();

    }
}

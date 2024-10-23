/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.index.infra;

import com.chutneytesting.execution.infra.storage.jpa.ScenarioExecutionReportEntity;
import com.chutneytesting.index.domain.IndexRepository;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.stereotype.Component;

@Component
public class ScenarioExecutionReportIndexRepository {

    public static final String SCENARIO_EXECUTION_REPORT = "scenario_execution_report";
    public static final String WHAT = "what";
    public static final String SCENARIO_EXECUTION_ID = "scenarioExecutionId";
    public static final String REPORT = "report";
    private final IndexRepository indexRepository;

    public ScenarioExecutionReportIndexRepository(IndexRepository indexRepository) {
        this.indexRepository = indexRepository;
    }

    public void save(ScenarioExecutionReportEntity report) {
        Document document = new Document();
        document.add(new StoredField(SCENARIO_EXECUTION_ID, report.scenarioExecutionId()));
        document.add(new StoredField(REPORT, report.getReport()));
        document.add(new StoredField(WHAT, SCENARIO_EXECUTION_REPORT));
        indexRepository.index(document);
    }

    public void remove(Long scenarioExecutionId) {
        Query whatQuery = new TermQuery(new Term(WHAT, SCENARIO_EXECUTION_REPORT));
        Query idQuery = new TermQuery(new Term(SCENARIO_EXECUTION_ID, scenarioExecutionId.toString()));
        BooleanQuery query = new BooleanQuery.Builder()
            .add(idQuery, BooleanClause.Occur.MUST)
            .add(whatQuery, BooleanClause.Occur.MUST)
            .build();
        indexRepository.delete(query);
    }


    public List<Long> idsByKeywordInReport(String keyword) {

        try {
            Query whatQuery = new TermQuery(new Term(WHAT, SCENARIO_EXECUTION_REPORT));

            QueryParser parser = new QueryParser(REPORT, new StandardAnalyzer());
            Query reportQuery = parser.parse(keyword);

            BooleanQuery query = new BooleanQuery.Builder()
                .add(reportQuery, BooleanClause.Occur.MUST)
                .add(whatQuery, BooleanClause.Occur.MUST)
                .build();

            return indexRepository.search(query, 100)
                .stream()
                .map(doc -> doc.get(SCENARIO_EXECUTION_ID))
                .map(Long::parseLong)
                .toList();

        } catch (ParseException e) {
            throw new RuntimeException("Could not parse keyword: " + keyword, e);
        }
    }
}

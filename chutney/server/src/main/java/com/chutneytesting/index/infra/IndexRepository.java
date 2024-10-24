/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.index.infra;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.springframework.stereotype.Repository;

@Repository
public class IndexRepository {

    private final IndexWriter indexWriter;
    private final Directory indexDirectory;

    public IndexRepository(IndexConfig config) {
        this.indexDirectory = config.directory();
        this.indexWriter = config.indexWriter();
    }

    public void index(Document document) {
        try {
            this.indexWriter.addDocument(document);
            this.indexWriter.commit();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't index data", e);
        }
    }

    public List<Document> search(Query query, int limit, Sort sort) {
        List<Document> result = new ArrayList<>();
        try (DirectoryReader reader = DirectoryReader.open(indexDirectory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            ScoreDoc[] hits = searcher.search(query, limit, sort).scoreDocs;
            StoredFields storedFields = searcher.storedFields();
            for (ScoreDoc hit : hits){
                result.add(storedFields.document(hit.doc));
            }
        } catch (IOException ignored) {
        }
        return result;
    }

    public void delete(Query query) {
        try {
            indexWriter.deleteDocuments(query);
            indexWriter.commit();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't delete index using query " + query, e);
        }
    }

    public void deleteAll() {
        try {
            indexWriter.deleteAll();
            indexWriter.commit();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't delete all indexes", e);
        }
    }
}


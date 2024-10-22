/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.index.infra;

import com.chutneytesting.index.domain.IndexRepository;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

@Repository
public class OnDiskIndexRepository implements IndexRepository {

    final IndexWriter indexWriter;
    final Directory indexDirectory;

    public OnDiskIndexRepository(@Value("${chutney.index-folder:~/.chutney/index}") String indexDir) {
        try {
            this.indexDirectory = FSDirectory.open(Paths.get(indexDir));
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            this.indexWriter = new IndexWriter(indexDirectory, config);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't initialize index directory", e);
        }

    }

    @Override
    public void index(Document document) {
        try {
            this.indexWriter.addDocument(document);
            this.indexWriter.commit();
        } catch (IOException e) {
            throw new RuntimeException("Couldn't index data", e);
        }
    }

    @Override
    public List<Document> search(Query query, int limit) {
        List<Document> result = new ArrayList<>();
        try (DirectoryReader reader = DirectoryReader.open(indexDirectory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            ScoreDoc[] hits = searcher.search(query, limit).scoreDocs;
            StoredFields storedFields = searcher.storedFields();
            for (ScoreDoc hit : hits){
                result.add(storedFields.document(hit.doc));
            }
        } catch (IOException ignored) {
        }
        return result;
    }

    @Override
    public void delete(Query query) {
        try {
            indexWriter.deleteDocuments(query);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't delete index using query " + query, e);
        }
    }
}


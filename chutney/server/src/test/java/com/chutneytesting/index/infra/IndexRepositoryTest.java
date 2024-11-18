/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.index.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.chutneytesting.tools.file.FileUtils;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.WildcardQuery;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class IndexRepositoryTest {

    private IndexRepository indexRepository;
    private IndexConfig indexConfig;
    private Path tmpDir;

    @BeforeEach
    public void setUp() throws IOException {
        this.tmpDir = Files.createTempDirectory("index");
        indexConfig = new OnDiskIndexConfig(tmpDir.toString());
        indexRepository = new IndexRepository(indexConfig);
    }

    @AfterEach
    void tearDown() {
        FileUtils.deleteFolder(tmpDir);
    }

    @Test
    void should_index_document() throws IOException {
        // Given
        Document doc = new Document();
        doc.add(new StringField("id", "1", Field.Store.YES));
        doc.add(new StringField("title", "Indexed Document", Field.Store.YES));

        // When
        indexRepository.index(doc);

        // Then
        assertTrue(DirectoryReader.indexExists(indexConfig.directory()), "Index should exist after document is indexed.");

    }

    @Test
    void should_search_by_query() {
        //Given
        Document doc = new Document();
        doc.add(new StringField("id", "1", Field.Store.YES));
        doc.add(new StringField("title", "Searchable Document", Field.Store.YES));
        indexRepository.index(doc);

        doc = new Document();
        doc.add(new StringField("id", "2", Field.Store.YES));
        doc.add(new StringField("title", "other title", Field.Store.YES));
        indexRepository.index(doc);

        Query query = new TermQuery(new Term("title", "Searchable Document"));

        // When
        List<Document> results = indexRepository.search(query, 10, Sort.RELEVANCE);

        // Then
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getField("id").stringValue()).isEqualTo("1");
    }

    @Test
    void should_delete_document_from_index() {
        // Given
        Document doc = new Document();
        doc.add(new StringField("id", "1", Field.Store.YES));
        doc.add(new StringField("title", "Document to be deleted", Field.Store.YES));
        indexRepository.index(doc);

        doc = new Document();
        doc.add(new StringField("id", "2", Field.Store.YES));
        doc.add(new StringField("title", "other title", Field.Store.YES));
        indexRepository.index(doc);

        Query query = new TermQuery(new Term("title", "Document to be deleted"));

        // When
        indexRepository.delete(query);

        // Then
        List<Document> resultsAfterDelete = indexRepository.search(query, 10, Sort.RELEVANCE);
        assertEquals(0, resultsAfterDelete.size());

        query = new TermQuery(new Term("title", "other title"));
        List<Document> results = indexRepository.search(query, 10, Sort.RELEVANCE);
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getField("id").stringValue()).isEqualTo("2");
    }

    @Test
    void should_clean_index() {
        // Given
        Document doc = new Document();
        doc.add(new StringField("id", "1", Field.Store.YES));
        doc.add(new StringField("title", "Document to be deleted", Field.Store.YES));
        indexRepository.index(doc);

        doc = new Document();
        doc.add(new StringField("id", "2", Field.Store.YES));
        doc.add(new StringField("title", "other title", Field.Store.YES));
        indexRepository.index(doc);

        // When
        indexRepository.deleteAll();

        // Then
        Query query = new WildcardQuery(new Term("title", "*"));
        List<Document> resultsAfterDeleteAll = indexRepository.search(query, 10, Sort.RELEVANCE);
        assertEquals(0, resultsAfterDeleteAll.size());

    }

}


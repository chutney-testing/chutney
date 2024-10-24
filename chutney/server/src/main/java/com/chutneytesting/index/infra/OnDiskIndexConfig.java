/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.index.infra;

import static com.chutneytesting.tools.file.FileUtils.initFolder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class OnDiskIndexConfig implements  IndexConfig{
    private final IndexWriter indexWriter;
    private final Directory indexDirectory;

    public OnDiskIndexConfig(@Value("${chutney.index-folder:~/.chutney/index}") String indexDir) {
        try {
            Path path = Paths.get(indexDir);
            initFolder(path);
            this.indexDirectory = FSDirectory.open(path);
            IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
            this.indexWriter = new IndexWriter(indexDirectory, config);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't open index directory", e);
        }
    }

    @Override
    public Directory directory() {
        return indexDirectory;
    }

    @Override
    public IndexWriter indexWriter() {
        return indexWriter;
    }
}

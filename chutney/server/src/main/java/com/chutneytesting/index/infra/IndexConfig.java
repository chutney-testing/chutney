/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.index.infra;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;

public interface IndexConfig {
    Directory directory();
    IndexWriter indexWriter();
}

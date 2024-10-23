/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.index.domain;

import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;

public interface IndexRepository {

    void index(Document document);
    List<Document> search(Query query, int limit);

    void delete(Query query);
}

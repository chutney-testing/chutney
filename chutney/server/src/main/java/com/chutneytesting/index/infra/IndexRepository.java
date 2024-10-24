/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.index.infra;

import java.util.List;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;

public interface IndexRepository {

    void index(Document document);
    List<Document> search(Query query, int limit, Sort sort);
    void delete(Query query);
}

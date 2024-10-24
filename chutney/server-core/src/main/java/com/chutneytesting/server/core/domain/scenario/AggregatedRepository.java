/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.scenario;

import java.util.List;
import java.util.Optional;

public interface AggregatedRepository<T extends TestCase> {

    String save(T scenario);

    Optional<T> findById(String testCaseId);

    Optional<TestCaseMetadata> findMetadataById(String testCaseId);

    List<TestCaseMetadata> findAll();

    List<TestCaseMetadata> findAllByDatasetId(String datasetId);

    void removeById(String testCaseId);

    Optional<Integer> lastVersion(String testCaseId);

    List<TestCaseMetadata> search(String textFilter);

    Optional<TestCase> findExecutableById(String testCaseId);
}

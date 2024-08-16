/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.scenario.campaign;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import com.chutneytesting.server.core.domain.scenario.TestCase;
import java.util.Objects;

public record TestCaseDataset(TestCase testcase, String datasetId) {

    @Override
    public String datasetId() {
        return ofNullable(datasetId).filter(not(String::isBlank)).orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestCaseDataset that = (TestCaseDataset) o;
        return Objects.equals(testcase.id(), that.testcase.id()) &&
            Objects.equals(datasetId(), that.datasetId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(testcase.id(), datasetId());
    }
}

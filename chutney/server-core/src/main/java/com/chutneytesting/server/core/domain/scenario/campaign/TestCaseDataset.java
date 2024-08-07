/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.scenario.campaign;

import static java.util.Optional.ofNullable;
import static java.util.function.Predicate.not;

import com.chutneytesting.server.core.domain.scenario.ExternalDataset;
import com.chutneytesting.server.core.domain.scenario.TestCase;
import java.util.Objects;

public record TestCaseDataset(TestCase testcase, ExternalDataset dataset) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestCaseDataset that = (TestCaseDataset) o;
        return Objects.equals(testcase.id(), that.testcase.id()) &&
            ((dataset == null && that.dataset == null) ||
                (dataset != null && that.dataset != null &&
                    (Objects.equals(dataset.getDatasetId(), that.dataset().getDatasetId()) &&
                        Objects.equals(dataset.getConstants(), that.dataset().getConstants()) &&
                        Objects.equals(dataset.getDatatable(), that.dataset().getDatatable())
                    )
                )
            );
    }

    @Override
    public int hashCode() {
        return Objects.hash(testcase.id(), dataset());
    }
}

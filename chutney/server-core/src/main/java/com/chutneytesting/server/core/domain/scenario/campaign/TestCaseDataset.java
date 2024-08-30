/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.scenario.campaign;

import com.chutneytesting.server.core.domain.dataset.DataSet;
import com.chutneytesting.server.core.domain.scenario.TestCase;
import java.util.Objects;

public record TestCaseDataset(TestCase testcase, DataSet dataset) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestCaseDataset that = (TestCaseDataset) o;
        return Objects.equals(testcase.id(), that.testcase.id()) &&
            ((dataset == null && that.dataset == null) ||
                (dataset != null && that.dataset != null &&
                    (Objects.equals(dataset.id, that.dataset().id) &&
                        Objects.equals(dataset.constants, that.dataset().constants) &&
                        Objects.equals(dataset.datatable, that.dataset().datatable)
                    )
                )
            );
    }

    @Override
    public int hashCode() {
        return Objects.hash(testcase.id(), dataset());
    }
}

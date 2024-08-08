/*
 *  Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

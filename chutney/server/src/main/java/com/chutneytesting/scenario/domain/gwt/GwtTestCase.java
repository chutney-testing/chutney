/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.scenario.domain.gwt;

import com.chutneytesting.server.core.domain.scenario.TestCase;
import com.chutneytesting.server.core.domain.scenario.TestCaseMetadata;
import com.chutneytesting.server.core.domain.scenario.TestCaseMetadataImpl;
import java.util.Objects;

public class GwtTestCase implements TestCase {

    public final TestCaseMetadataImpl metadata;
    public final GwtScenario scenario;

    private GwtTestCase(TestCaseMetadataImpl metadata, GwtScenario scenario) {
        this.metadata = metadata;
        this.scenario = scenario;
    }

    @Override
    public TestCaseMetadata metadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "GwtTestCase{" +
            "metadata=" + metadata +
            ", scenario=" + scenario +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GwtTestCase that = (GwtTestCase) o;
        return metadata.equals(that.metadata) &&
            scenario.equals(that.scenario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadata, scenario);
    }

    public static GwtTestCaseBuilder builder() {
        return new GwtTestCaseBuilder();
    }

    public static class GwtTestCaseBuilder {

        private TestCaseMetadataImpl metadata;
        private GwtScenario scenario;

        private GwtTestCaseBuilder() {}

        public GwtTestCase build() {
            return new GwtTestCase(
                metadata,
                scenario
            );
        }

        public GwtTestCaseBuilder withMetadata(TestCaseMetadataImpl metadata) {
            this.metadata = metadata;
            return this;
        }

        public GwtTestCaseBuilder withScenario(GwtScenario scenario) {
            this.scenario = scenario;
            return this;
        }

        public GwtTestCaseBuilder from(GwtTestCase testCase) {
            withMetadata(testCase.metadata);
            withScenario(testCase.scenario);
            return this;
        }
    }
}

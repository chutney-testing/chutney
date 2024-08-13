/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.scenario.domain.raw;

import static java.util.Optional.ofNullable;

import com.chutneytesting.server.core.domain.scenario.TestCase;
import com.chutneytesting.server.core.domain.scenario.TestCaseMetadata;
import com.chutneytesting.server.core.domain.scenario.TestCaseMetadataImpl;
import java.util.Objects;

public class RawTestCase implements TestCase {

    public final TestCaseMetadataImpl metadata;
    public final String scenario; // Blob

    public RawTestCase(TestCaseMetadataImpl metadata, String scenario) {
        this.metadata = metadata;
        this.scenario = scenario;
    }

    @Override
    public TestCaseMetadata metadata() {
        return metadata;
    }

    @Override
    public String toString() {
        return "RawTestCase{" +
            "metadata=" + metadata +
            ", scenario=" + scenario +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RawTestCase that = (RawTestCase) o;
        return Objects.equals(metadata, that.metadata) &&
            Objects.equals(scenario, that.scenario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(metadata, scenario);
    }

    public static RawTestCaseBuilder builder() {
        return new RawTestCaseBuilder();
    }

    public static class RawTestCaseBuilder {

        private TestCaseMetadataImpl metadata;
        private String scenario;

        private RawTestCaseBuilder() {}

        public RawTestCase build() {
            return new RawTestCase(
                ofNullable(metadata).orElseGet(() -> TestCaseMetadataImpl.builder().build()),
                ofNullable(scenario).orElse("")
            );
        }

        public RawTestCaseBuilder withMetadata(TestCaseMetadataImpl metadata) {
            this.metadata = metadata;
            return this;
        }

        public RawTestCaseBuilder withScenario(String scenario) {
            this.scenario = scenario;
            return this;
        }
    }
}

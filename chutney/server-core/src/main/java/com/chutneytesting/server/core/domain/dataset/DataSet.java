/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.dataset;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

public class DataSet {

    public static Comparator<DataSet> datasetComparator = Comparator.comparing(DataSet::getName, String.CASE_INSENSITIVE_ORDER);
    public static DataSet NO_DATASET = new DataSet(null, null, null, null, null, emptyMap(), emptyList(), emptySet(), emptyMap(), emptySet());

    public final String id;
    public final String name;
    public final String description;
    public final Instant creationDate;
    public final List<String> tags;
    public final Map<String, String> constants;
    public final List<Map<String, String>> datatable;
    public final Set<String> campaignUsage;
    public final Map<String, Set<String>> scenarioInCampaignUsage;
    public final Set<String> scenarioUsage;

    private DataSet(String id, String name, String description, Instant creationDate, List<String> tags, Map<String, String> constants, List<Map<String, String>> datatable, Set<String> campaignUsage, Map<String, Set<String>> scenarioInCampaignUsage, Set<String> scenarioUsage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.creationDate = creationDate;
        this.tags = tags;
        this.constants = constants;
        this.datatable = datatable;
        this.campaignUsage = campaignUsage;
        this.scenarioInCampaignUsage = scenarioInCampaignUsage;
        this.scenarioUsage = scenarioUsage;
    }

    private String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataSet dataSet = (DataSet) o;
        return Objects.equals(name, dataSet.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "DataSet{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", creationDate=" + creationDate +
            ", tags=" + tags +
            ", constants=" + constants +
            ", datatable=" + datatable +
            '}';
    }

    public static DataSetBuilder builder() {
        return new DataSetBuilder();
    }

    public static class DataSetBuilder {

        private String id;
        private String name;
        private String description;
        private Instant creationDate;
        private List<String> tags;
        private Map<String, String> constants;
        private List<Map<String, String>> datatable;
        public Set<String> campaignUsage;
        public Set<String> scenarioUsage;
        public Map<String, Set<String>> scenarioInCampaignUsage;

        private DataSetBuilder() {
        }

        public DataSet build() {
            if (ofNullable(id).map(String::isBlank).orElse(false)) {
                throw new IllegalArgumentException("Dataset id cannot be blank");
            }

            return new DataSet(
                id,
                prettify(ofNullable(name).orElseThrow(() -> new IllegalArgumentException("Dataset name mandatory"))),
                ofNullable(description).orElse(""),
                ofNullable(creationDate).orElseGet(() -> Instant.now().truncatedTo(MILLIS)),
                (ofNullable(tags).orElse(emptyList())).stream().map(String::toUpperCase).map(String::strip).collect(toList()),
                cleanConstants(ofNullable(constants).orElse(emptyMap())),
                cleanDatatable(ofNullable(datatable).orElse(emptyList())),
                ofNullable(campaignUsage).orElse(emptySet()),
                ofNullable(scenarioInCampaignUsage).orElse(emptyMap()),
                ofNullable(scenarioUsage).orElse(emptySet())
            );
        }

        private String prettify(String name) {
            return name.strip().replaceAll(" +", " ");
        }

        public DataSetBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public DataSetBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public DataSetBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public DataSetBuilder withCreationDate(Instant creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public DataSetBuilder withTags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public DataSetBuilder withConstants(Map<String, String> constants) {
            this.constants = constants;
            return this;
        }

        public DataSetBuilder withDatatable(List<Map<String, String>> datatable) {
            this.datatable = datatable;
            return this;
        }

        public DataSetBuilder withScenarioUsage(Set<String> scenarioUsage) {
            this.scenarioUsage = scenarioUsage;
            return this;
        }

        public DataSetBuilder withCampaignUsage(Set<String> campaignUsage) {
            this.campaignUsage = campaignUsage;
            return this;
        }

        public DataSetBuilder withScenarioInCampaign(Map<String, Set<String>> scenarioInCampaignUsage) {
            this.scenarioInCampaignUsage = scenarioInCampaignUsage;
            return this;
        }

        public DataSetBuilder fromDataSet(DataSet dataset) {
            return new DataSetBuilder()
                .withId(dataset.id)
                .withName(dataset.name)
                .withDescription(dataset.description)
                .withCreationDate(dataset.creationDate)
                .withTags(dataset.tags)
                .withConstants(dataset.constants)
                .withDatatable(dataset.datatable)
                .withCampaignUsage(dataset.campaignUsage)
                .withScenarioUsage(dataset.scenarioUsage)
                .withScenarioInCampaign(dataset.scenarioInCampaignUsage);
        }

        private Map<String, String> cleanConstants(Map<String, String> constants) {
            // Remove empty keys
            return constants.entrySet().stream()
                .filter(e -> isNotBlank(e.getKey()))
                .collect(Collectors.toMap(e -> e.getKey().trim(), e -> e.getValue().trim()));
        }

        private List<Map<String, String>> cleanDatatable(List<Map<String, String>> datatable) {
            // Remove empty keys and empty lines
            return datatable.stream()
                .map(this::cleanConstants)
                .filter(this::linesWithAtLeastOneNonBlankValue)
                .map(m -> m.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().trim(), e -> e.getValue().trim())))
                .collect(toList());
        }

        private boolean linesWithAtLeastOneNonBlankValue(Map<String, String> map) {
            return map.values().stream().anyMatch(StringUtils::isNotBlank);
        }
    }
}

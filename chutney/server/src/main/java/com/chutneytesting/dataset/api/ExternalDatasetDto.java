/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.dataset.api;

import static java.util.Collections.emptyList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableExternalDatasetDto.class)
@JsonDeserialize(as = ImmutableExternalDatasetDto.class)
@Value.Style(jdkOnly = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface ExternalDatasetDto {
    Optional<String> datasetId();

    @Value.Default()
    @JsonProperty("uniqueValues")
    default List<KeyValue> constants() {
        return emptyList();
    }

    @Value.Default()
    @JsonProperty("multipleValues")
    default List<List<KeyValue>> datatable() {
        return emptyList();
    }
}

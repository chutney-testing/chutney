/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.dataset.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExecutionDatasetDto {
    private String id;
    @JsonProperty("uniqueValues")
    private List<KeyValue> constants;
    @JsonProperty("multipleValues")
    private List<List<KeyValue>> datatable;

    public String getId() {
        return id;
    }

    public ExecutionDatasetDto setId(String id) {
        this.id = id;
        return this;
    }

    public List<KeyValue> getConstants() {
        return constants;
    }

    public ExecutionDatasetDto setConstants(List<KeyValue> constants) {
        this.constants = constants;
        return this;
    }

    public List<List<KeyValue>> getDatatable() {
        return datatable;
    }

    public ExecutionDatasetDto setDatatable(List<List<KeyValue>> datatable) {
        this.datatable = datatable;
        return this;
    }
}

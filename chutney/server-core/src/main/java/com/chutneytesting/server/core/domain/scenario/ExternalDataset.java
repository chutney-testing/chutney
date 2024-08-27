/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.scenario;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.Map;

public class ExternalDataset {
    private final String datasetId;
    private final Map<String, String> constants;
    private final List<Map<String, String>> datatable;

    public ExternalDataset(String datasetId) {
        this.datasetId = datasetId;
        this.constants = emptyMap();
        this.datatable = emptyList();
    }

    public ExternalDataset(Map<String, String> constants, List<Map<String, String>> datatable) {
        this.datasetId = null;
        this.constants = constants;
        this.datatable = datatable;
    }

    public ExternalDataset(String datasetId, Map<String, String> constants, List<Map<String, String>> datatable) {
        this.datasetId = datasetId;
        this.constants = constants;
        this.datatable = datatable;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public Map<String, String> getConstants() {
        return constants;
    }

    public List<Map<String, String>> getDatatable() {
        return datatable;
    }
}

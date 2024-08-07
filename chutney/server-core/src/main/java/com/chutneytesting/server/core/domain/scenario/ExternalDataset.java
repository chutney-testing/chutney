/*
 *  Copyright 2017-2023 Enedis
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

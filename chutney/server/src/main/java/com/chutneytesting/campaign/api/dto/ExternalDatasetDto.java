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

package com.chutneytesting.campaign.api.dto;

import java.util.List;
import java.util.Map;

public class ExternalDatasetDto {
    private final String datasetId;
    private final Map<String, String> constants;
    private final List<Map<String, String>> datatable;

    public ExternalDatasetDto(String datasetId, Map<String, String> constants, List<Map<String, String>> datatable) {
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

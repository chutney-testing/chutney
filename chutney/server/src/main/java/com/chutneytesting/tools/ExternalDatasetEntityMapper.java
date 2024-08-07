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

package com.chutneytesting.tools;

import com.chutneytesting.server.core.domain.scenario.ExternalDataset;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;

public class ExternalDatasetEntityMapper {

    private static ObjectMapper mapper = new ObjectMapper();

    private static List<Map<String, String>> datasetDatatableFromString(String datasetDatatable) {
        return mapper.convertValue(datasetDatatable, new TypeReference<>() {});
    }

    private static Map<String, String> datasetConstantsFromString(String datasetConstants) {
        return mapper.convertValue(datasetConstants, new TypeReference<>() {});
    }

    public static String datasetDatatableToString(List<Map<String, String>> datatable) {
        try {
            return mapper.writeValueAsString(datatable);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static String datasetConstantsToString(Map<String, String> constants) {
        try {
            return mapper.writeValueAsString(constants);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static ExternalDataset getExternalDataset(String datasetId, String datasetConstants, String datasetDatatable) {
        if (datasetId == null && (datasetConstants == null || datasetConstants.isEmpty()) && (datasetDatatable == null || datasetDatatable.isEmpty())) {
            return null;
        }
        return new ExternalDataset(
            datasetId,
            datasetConstantsFromString(datasetConstants),
            datasetDatatableFromString(datasetDatatable)
        );
    }
}

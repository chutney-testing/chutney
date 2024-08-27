/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.dataset;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import com.chutneytesting.server.core.domain.scenario.ExternalDataset;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ExternalDatasetEntityMapper {

    private static ObjectMapper mapper = new ObjectMapper();

    private static List<Map<String, String>> datasetDatatableFromString(String datasetDatatable) {
        if (datasetDatatable == null) {
            return null;
        }
        try {
            return mapper.readValue(datasetDatatable, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return emptyList();
        }
    }

    private static Map<String, String> datasetConstantsFromString(String datasetConstants) {
        if (datasetConstants == null) {
            return null;
        }
        try {
            return mapper.readValue(datasetConstants, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            return emptyMap();
        }
    }

    public static String datasetDatatableToString(List<Map<String, String>> datatable) {
        if (datatable == null || datatable.isEmpty()) {
            return null;
        }
        try {
            return mapper.writeValueAsString(datatable);
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    public static String datasetConstantsToString(Map<String, String> constants) {
        if (constants == null || constants.isEmpty()) {
            return null;
        }
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

    public static boolean compareExternalDataset(ExternalDataset dataset1, ExternalDataset dataset2) {
        if (dataset1 == null && dataset2 == null) {
            return true;
        } else if (dataset1 == null || dataset2 == null) {
            return false;
        } else if (dataset1.getDatasetId() != null) {
            return dataset1.getDatasetId().equals(dataset2.getDatasetId());
        } else if (compareConstantsDataset(dataset2.getConstants(), dataset1.getConstants()) &&
            compareDatatableDataset(dataset2.getDatatable(), dataset1.getDatatable())) {
            return true;
        }
        return false;
    }

    private static boolean compareConstantsDataset(Map<String, String> constant1, Map<String, String> constant2) {
        if (constant1.size() != constant2.size()) {
            return false;
        }
        for (Map.Entry<String, String> entry : constant1.entrySet()) {
            if (!constant2.containsKey(entry.getKey()) || !entry.getValue().equals(constant2.get(entry.getKey()))) {
                return false;
            }
        }
        return true;
    }

    private static boolean compareDatatableDataset(List<Map<String, String>> datatable1, List<Map<String, String>> datatable2) {
        if (datatable1.size() != datatable2.size()) {
            return false;
        }

        List<String> sortedList1 = datatable1.stream()
            .map(map -> map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(",")))
            .sorted()
            .toList();

        List<String> sortedList2 = datatable2.stream()
            .map(map -> map.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(",")))
            .sorted()
            .toList();

        return sortedList1.equals(sortedList2);
    }
}

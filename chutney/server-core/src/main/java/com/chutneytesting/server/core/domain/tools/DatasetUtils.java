/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.tools;

import com.chutneytesting.server.core.domain.dataset.DataSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DatasetUtils {

    public static boolean compareDataset(DataSet dataset1, DataSet dataset2) {
        if ((dataset1 == null || DataSet.NO_DATASET.equals(dataset1)) && (dataset2 == null || DataSet.NO_DATASET.equals(dataset2))) {
            return true;
        } else if (dataset1 == null || dataset2 == null) {
            return false;
        } else if (dataset1.id != null) {
            return dataset1.id.equals(dataset2.id);
        } else return compareConstantsDataset(dataset2.constants, dataset1.constants) &&
            compareDatatableDataset(dataset2.datatable, dataset1.datatable);
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

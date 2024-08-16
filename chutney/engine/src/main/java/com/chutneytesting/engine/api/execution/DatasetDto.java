/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.api.execution;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DatasetDto {

    public final Map<String, String> constants;
    public final List<Map<String, String>> datatable;

    public DatasetDto(Map<String, String> constants, List<Map<String, String>> datatable) {
        this.constants = Collections.unmodifiableMap(constants);
        this.datatable = datatable.stream()
            .map(Collections::unmodifiableMap)
            .toList();
    }

    public DatasetDto() {
        this(emptyMap(), emptyList());
    }
}

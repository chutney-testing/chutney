/*
 * Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

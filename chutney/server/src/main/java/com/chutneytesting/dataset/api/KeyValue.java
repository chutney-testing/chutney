/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.dataset.api;

import static java.util.Optional.ofNullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableKeyValue.class)
@JsonDeserialize(as = ImmutableKeyValue.class)
@Value.Style(jdkOnly = true)
public interface KeyValue {

    String key();

    @Value.Default
    default String value() {
        return "";
    }

    static List<KeyValue> fromMap(Map<String, String> map) {
        return map.keySet().stream()
            .map((key) -> (KeyValue) ImmutableKeyValue.builder()
                .key(key)
                .value(map.get(key))
                .build()
            )
            .toList();
    }


    static Map<String, String> toMap(List<KeyValue> list) {
        return ofNullable(list)
            .map(l -> l.stream()
                .filter(kv -> StringUtils.isNoneBlank(kv.key()))
                .collect(Collectors.toUnmodifiableMap(KeyValue::key, KeyValue::value, (k1, k2) -> k1)))
            .orElseGet(Map::of);
    }
}

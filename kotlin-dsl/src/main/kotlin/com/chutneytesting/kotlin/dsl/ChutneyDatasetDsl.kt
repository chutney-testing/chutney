/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.dsl

data class Dataset(
    val name: String,
    val description: String = "",
    val uniqueValues: Set<KeyValue> = emptySet(),
    val multipleValues: List<List<KeyValue>> = emptyList(),
    val tags: List<String> = emptyList()
) {
    val id: String = name.replace(" ", "_")

    data class KeyValue(val key: String, val value: String)
}

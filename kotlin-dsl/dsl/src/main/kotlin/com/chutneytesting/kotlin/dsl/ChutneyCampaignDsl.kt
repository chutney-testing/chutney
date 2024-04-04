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

package com.chutneytesting.kotlin.dsl

data class Campaign(
    val id: Int? = null,
    val title: String,
    val description: String? = null,
    val environment: String = "DEFAULT",
    val parallelRun: Boolean = false,
    val retryAuto: Boolean = false,
    val datasetId: String? = null,
    val scenarioIds: List<Int> = emptyList(),
    val tags: Set<String> = emptySet()
)

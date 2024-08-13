/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.server

import com.google.gson.JsonObject
import com.intellij.openapi.util.Key

interface ChutneyServerOutputListener {
    fun onOutputAvailable(text: String, outputType: Key<*>)
    fun onEvent(obj: JsonObject)
}

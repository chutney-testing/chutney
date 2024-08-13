/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea.util

import com.intellij.openapi.util.io.FileUtil

fun sanitizeFilename(inputName: String): String {
    return FileUtil.sanitizeFileName(inputName, false, " ")
}

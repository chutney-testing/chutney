package com.chutneytesting.idea.util

import com.intellij.openapi.util.io.FileUtil

fun sanitizeFilename(inputName: String): String {
    return FileUtil.sanitizeFileName(inputName, false, " ")
}

package com.chutneytesting.idea

import com.intellij.json.JsonLanguage

class ChutneyLanguage : JsonLanguage("Chutney", "application/json") {
    companion object {
        val INSTANCE = ChutneyLanguage()
    }
}

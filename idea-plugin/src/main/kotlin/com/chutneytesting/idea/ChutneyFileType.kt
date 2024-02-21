package com.chutneytesting.idea

import com.intellij.json.JsonFileType
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon


class ChutneyFileType : JsonFileType(ChutneyLanguage.INSTANCE) {

    override fun getName(): String {
        return "Chutney"
    }

    override fun getDescription(): String {
        return "Chutney"
    }

    override fun getDefaultExtension(): String {
        return DEFAULT_EXTENSION
    }

    override fun getIcon(): Icon? {
        return ICON
    }

    companion object {
        val INSTANCE: ChutneyFileType = ChutneyFileType()
        val DEFAULT_EXTENSION = "chutney"
        val ICON = IconLoader.getIcon("/icons/Chutney.svg", ChutneyFileType::class.java)
    }


}

package com.chutneytesting.idea.completion.field.model

import org.apache.commons.lang.StringUtils

class StringField : Field {
    constructor(name: String?, required: Boolean) : super(name!!, required)
    constructor(name: String?) : super(name!!, false)

    override fun getJsonPlaceholderSuffix(indentation: Int): String? {
        return ": \"$CARET\""
    }

    override fun getYamlPlaceholderSuffix(indentation: Int): String? {
        return ": $CARET"
    }

    override fun getCompleteJson(indentation: Int): String? {
        val leftPadding = StringUtils.repeat(" ", indentation)
        return "$leftPadding\"$name\": \"$CARET\""
    }

    override fun getCompleteYaml(indentation: Int): String? {
        val leftPadding = StringUtils.repeat(" ", indentation)
        return "$leftPadding$name: $CARET"
    }

    companion object {
        const val CARET = "<caret>"
    }
}

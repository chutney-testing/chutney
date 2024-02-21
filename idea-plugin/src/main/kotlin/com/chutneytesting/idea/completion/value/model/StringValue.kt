package com.chutneytesting.idea.completion.value.model

class StringValue(value: String) : Value(value) {

    override val isQuotable: Boolean
        get() = true
}

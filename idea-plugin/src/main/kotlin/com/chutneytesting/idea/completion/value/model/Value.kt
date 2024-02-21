package com.chutneytesting.idea.completion.value.model

abstract class Value internal constructor(val value: String) {

    abstract val isQuotable: Boolean
}

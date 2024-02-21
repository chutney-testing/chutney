package com.chutneytesting.idea.completion.field.model

abstract class Field @JvmOverloads internal constructor(val name: String = "unknown", val isRequired: Boolean = false) {

    abstract fun getJsonPlaceholderSuffix(indentation: Int): String?
    abstract fun getYamlPlaceholderSuffix(indentation: Int): String?
    abstract fun getCompleteJson(indentation: Int): String?
    abstract fun getCompleteYaml(indentation: Int): String?

}

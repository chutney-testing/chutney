package com.chutneytesting.idea.completion.field.model

import com.google.common.collect.ImmutableList
import org.apache.commons.lang.StringUtils

open class ObjectField : Field {
    constructor(name: String?) : super(name!!, false)
    constructor(name: String?, required: Boolean) : super(name!!, required)

    override fun getJsonPlaceholderSuffix(indentation: Int): String? {
        val sb = StringBuilder()
        val indentationPadding = StringUtils.repeat(" ", indentation)
        sb.append(": {\n")
            .append(printJsonChildren(indentation + 2))
            .append("\n")
            .append(indentationPadding)
            .append("}")
        return sb.toString()
    }

    override fun getYamlPlaceholderSuffix(indentation: Int): String? {
        return ":\n" + printYamlChildren(indentation + 2)
    }

    override fun getCompleteJson(indentation: Int): String? {
        val indentationPadding = StringUtils.repeat(" ", indentation)
        return indentationPadding + "\"" + name + "\"" + getJsonPlaceholderSuffix(indentation)
    }

    override fun getCompleteYaml(indentation: Int): String? {
        val indentationPadding = StringUtils.repeat(" ", indentation)
        return indentationPadding + name + getYamlPlaceholderSuffix(indentation)
    }

    private fun printJsonChildren(indentation: Int): String {
        if (children.isEmpty()) {
            return StringUtils.repeat(" ", indentation) + CARET
        }
        val sb = StringBuilder()
        for (field in children) {
            sb.append(field.getCompleteJson(indentation)).append(",\n")
        }
        if (sb.length > 1) {
            sb.deleteCharAt(sb.length - 1)
            sb.deleteCharAt(sb.length - 1)
        }
        return sb.toString()
    }

    private fun printYamlChildren(indentation: Int): String {
        if (children.isEmpty()) {
            return StringUtils.repeat(" ", indentation) + CARET
        }
        val sb = StringBuilder()
        for (field in children) {
            sb.append(field.getCompleteYaml(indentation)).append("\n")
        }
        if (sb.length > 0) {
            sb.deleteCharAt(sb.length - 1)
        }
        return sb.toString()
    }

    open val children: List<Field>
        get() = ImmutableList.of()

    companion object {
        const val CARET = "<caret>"
    }
}

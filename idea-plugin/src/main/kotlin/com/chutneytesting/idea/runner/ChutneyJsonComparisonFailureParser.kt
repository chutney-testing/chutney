package com.chutneytesting.idea.runner

import com.intellij.rt.execution.junit.ComparisonFailureData
import java.util.regex.Pattern


data class ChutneyComparisonFailureData(val actual: String, val expected: String)

private val ASSERT_EQUALS_PATTERN = Pattern.compile("found \\[(.*)\\], expected was \\[(.*)\\]", 34)

class ChutneyJsonComparisonFailureParser : TestComparisonFailureParser {
    override fun tryParse(message: String): ChutneyComparisonFailureData? {
        val comparisonFailureData = createExceptionNotification(message, ASSERT_EQUALS_PATTERN) ?: return null
        return ChutneyComparisonFailureData(comparisonFailureData.actual, comparisonFailureData.expected)
    }

    private fun createExceptionNotification(message: String, pattern: Pattern): ComparisonFailureData? {
        val matcher = pattern.matcher(message)
        return if (matcher.find()) {
            val expected = matcher.group(2).replace("\\\\n".toRegex(), "\n")
            val actual = matcher.group(1).replace("\\\\n".toRegex(), "\n")
            ComparisonFailureData(expected, actual)
        } else null
    }
}

interface TestComparisonFailureParser {
    fun tryParse(message: String): ChutneyComparisonFailureData?
}

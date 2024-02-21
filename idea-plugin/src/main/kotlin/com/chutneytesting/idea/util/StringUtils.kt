package com.chutneytesting.idea.util

object StringUtils {
    fun nextCharAfterSpacesAndQuotesIsColon(string: String): Boolean {
        for (i in 0 until string.length) {
            val c = string[i]
            if (c != ' ' && c != '"') {
                return c == ':'
            }
        }
        return false
    }

    fun nextCharAfterSpacesAndLineBreaksIsCurlyBraces(string: String): Boolean {
        for (i in 0 until string.length) {
            val c = string[i]
            if (c != ' ' && c != '\n') {
                return c == '}'
            }
        }
        return false
    }

    fun getNumberOfSpacesInRowStartingFromEnd(string: String): Int {
        var count = 0
        for (i in string.length - 1 downTo 0) {
            val c = string[i]
            if (c != ' ') {
                return count
            }
            count++
        }
        return count
    }

    fun removeAllQuotes(string: String): String {
        return string.replace("'", "").replace("\"", "")
    }

    fun hasSingleQuoteBeforeColonStartingFromEnd(string: String): Boolean {
        val lastIndexOfColon = string.lastIndexOf(":")
        val lastIndexOfSingleQuote = string.lastIndexOf("'")
        return lastIndexOfSingleQuote > lastIndexOfColon
    }

    fun nextCharAfterSpacesAndLineBreaksIsCommaStartingFromEnd(string: String): Boolean {
        for (i in string.length - 1 downTo 0) {
            val c = string[i]
            if (c != ' ' && c != '\n') {
                return c == ','
            }
        }
        return false
    }
}

package com.chutneytesting.idea.reference

import com.intellij.openapi.util.TextRange
import com.intellij.util.SmartList
import java.util.*

object SpringElTemplateParser {
    fun parse(expression: String): List<TextRange> {
        return parseExpressions(expression, "\${", "}")
    }

    private fun parseExpressions(expressionString: String, prefix: String, suffix: String): List<TextRange> {
        val expressions = SmartList<TextRange>()
        var startIdx = 0
        while (startIdx < expressionString.length) {
            val prefixIndex = expressionString.indexOf(prefix, startIdx)
            if (prefixIndex >= startIdx) {
                val afterPrefixIndex = prefixIndex + prefix.length
                var suffixIndex: Int
                suffixIndex = try {
                    skipToCorrectEndSuffix(prefix, suffix, expressionString, afterPrefixIndex)
                } catch (var9: Exception) {
                    return expressions
                }
                if (suffixIndex == -1) {
                    return expressions
                }
                expressions.add(TextRange.create(prefixIndex, suffixIndex + suffix.length))
                startIdx = suffixIndex + suffix.length
            } else {
                startIdx = expressionString.length
            }
        }
        return expressions
    }

    private fun isSuffixHere(expressionString: String, pos: Int, suffix: String): Boolean {
        var pos = pos
        var suffixPosition = 0
        var i = 0
        while (i < suffix.length && pos < expressionString.length) {
            if (expressionString[pos++] != suffix[suffixPosition++]) {
                return false
            }
            ++i
        }
        return suffixPosition == suffix.length
    }

    @Throws(Exception::class)
    private fun skipToCorrectEndSuffix(
        prefix: String,
        suffix: String,
        expressionString: String,
        afterPrefixIndex: Int
    ): Int {
        var pos = afterPrefixIndex
        val maxLen = expressionString.length
        val nextSuffix = expressionString.indexOf(suffix, afterPrefixIndex)
        return if (nextSuffix == -1) {
            -1
        } else {
            val stack: Stack<*>
            stack = Stack<Any?>()
            while (pos < maxLen && (!isSuffixHere(expressionString, pos, suffix) || !stack.isEmpty())) {
                val ch = expressionString[pos]
                when (ch) {
                    '"', '\'' -> {
                        val endLiteral = expressionString.indexOf(ch, pos + 1)
                        if (endLiteral == -1) {
                            throw Exception("Found non terminating string literal starting at position $pos")
                        }
                        pos = endLiteral
                    }
                    '(', '[', '{' -> stack.push(Bracket(ch, pos))
                    ')', ']', '}' -> {
                        if (stack.isEmpty()) {
                            throw Exception(
                                "Found closing '" + ch + "' at position " + pos + " without an opening '" + Bracket.theOpenBracketFor(
                                    ch
                                ) + "'"
                            )
                        }
                        val p = stack.pop() as Bracket
                        if (!p.compatibleWithCloseBracket(ch)) {
                            throw Exception("Found closing '" + ch + "' at position " + pos + " but most recent opening is '" + p.bracket + "' at position " + p.pos)
                        }
                    }
                }
                ++pos
            }
            if (!stack.isEmpty()) {
                val p = stack.pop() as Bracket
                throw Exception("Missing closing '" + Bracket.theCloseBracketFor(p.bracket) + "' for '" + p.bracket + "' at position " + p.pos)
            } else {
                if (!isSuffixHere(expressionString, pos, suffix)) -1 else pos
            }
        }
    }

    private class Bracket(internal val bracket: Char, internal val pos: Int) {
        fun compatibleWithCloseBracket(closeBracket: Char): Boolean {
            return if (bracket == '{') {
                closeBracket == '}'
            } else if (bracket == '[') {
                closeBracket == ']'
            } else {
                closeBracket == ')'
            }
        }

        companion object {
            fun theOpenBracketFor(closeBracket: Char): Char {
                return if (closeBracket == '}') {
                    '{'
                } else {
                    if (closeBracket == ']') '[' else '('
                }
            }

            fun theCloseBracketFor(openBracket: Char): Char {
                return if (openBracket == '{') {
                    '}'
                } else {
                    if (openBracket == '[') ']' else ')'
                }
            }
        }

    }
}

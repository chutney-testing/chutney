package com.chutneytesting.idea.completion

import java.util.*
import java.util.stream.Collectors

class PathExpression internal constructor(val path: String) {
    val currentPath: String
        get() = splitPath()[0]

    private fun splitPath(): Array<String> {
        return path.split("(?<!\\\\)\\.").toTypedArray()
    }

    fun afterFirst(): PathExpression {
        val parts = splitPath()
        val afterFirst = Arrays.stream(parts)
            .skip(1)
            .collect(Collectors.joining(SEPARATOR))
        return PathExpression(afterFirst)
    }

    fun beforeLast(): PathExpression {
        val parts = splitPath()
        val beforeLast = Arrays.stream(parts)
            .limit(if (parts.size == 1) 1 else parts.size - 1.toLong())
            .collect(Collectors.joining(SEPARATOR))
        return PathExpression(beforeLast)
    }

    val isEmpty: Boolean
        get() = path.isEmpty()

    fun last(): String {
        val paths = splitPath()
        return paths[paths.size - 1]
    }

    fun secondLast(): String {
        val paths = splitPath()
        return paths[paths.size - 2]
    }

    fun hasOnePath(): Boolean {
        return splitPath().size == 1
    }

    val isRoot: Boolean
        get() = "$" == path

    val isAnyKey: Boolean
        get() = ANY_KEY == last()

    val isAnyKeys: Boolean
        get() = ANY_KEYS == last()

    companion object {
        private const val ANY_KEY = "*"
        private const val ANY_KEYS = "**"
        private const val SEPARATOR = "."
    }

}

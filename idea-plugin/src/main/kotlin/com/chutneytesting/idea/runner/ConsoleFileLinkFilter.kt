package com.chutneytesting.idea.runner

import com.intellij.execution.filters.Filter
import com.intellij.execution.filters.Filter.Result
import com.intellij.execution.filters.OpenFileHyperlinkInfo
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.fileEditor.OpenFileDescriptor
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import org.intellij.lang.annotations.Language
import org.jetbrains.io.LocalFileFinder
import java.util.regex.Pattern


class ConsoleFileLinkFilter(val project: Project) : Filter {

    private val pattern = Pattern.compile(URI_PATTERN)

    override fun applyFilter(textLine: String, endPoint: Int): Filter.Result? {
        val startPoint = endPoint - textLine.length
        val matcher = pattern.matcher(textLine)
        if (matcher.find()) {
            val group = matcher.group(1)
            val filePath = group.substring("file://".length)
            val file = LocalFileFinder.findFile(FileUtil.toSystemIndependentName(filePath))
            if (file != null) {
                val fileDescriptor = OpenFileDescriptor(
                    project,
                    file,
                    if (matcher.group(3) == null) 0 else Integer.parseInt(matcher.group(3)) - 1, // line
                    if (matcher.group(5) == null) 0 else Integer.parseInt(matcher.group(5)) - 1 // column
                )
                return Result(
                    startPoint + matcher.start(),
                    startPoint + matcher.end(), OpenFileHyperlinkInfo(fileDescriptor)
                )
            }
        }
        return Result(startPoint, endPoint, null, TextAttributes())
    }

    companion object {
        @Language("RegExp")
        private const val URI_PATTERN = "(file://[a-zA-Z0-9:@/\\-_.]+\\.[a-z]+)(:(\\d+))?(:(\\d+))?"

    }
}



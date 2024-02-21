package com.chutneytesting.idea.util

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

object ProjectRootUtils {

    fun getContentRootForFile(project: Project, file: File): VirtualFile? {
        val fileIndex = ProjectRootManager.getInstance(project).fileIndex
        val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(file)
        return if (virtualFile != null) {
            fileIndex.getContentRootForFile(virtualFile)
        } else null
    }

    fun getRootRelativePath(project: Project, filePath: String): String? {
        val contentRoot = getContentRootForFile(project, File(filePath))
        val virtualFile = LocalFileSystem.getInstance().findFileByPath(filePath)
        if (contentRoot == null || virtualFile == null) {
            return null
        }
        return if (contentRoot == virtualFile) {
            contentRoot.name
        } else FileUtil.getRelativePath(contentRoot.path, virtualFile.path, '/')
    }
}

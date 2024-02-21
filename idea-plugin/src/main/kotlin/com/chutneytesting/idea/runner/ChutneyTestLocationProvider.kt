package com.chutneytesting.idea.runner

import com.intellij.execution.Location
import com.intellij.execution.PsiLocation
import com.intellij.execution.testframework.sm.runner.SMTestLocator
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiManager
import com.intellij.psi.search.GlobalSearchScope
import java.io.File

class ChutneyTestLocationProvider : SMTestLocator {
    override fun getLocation(
        protocol: String,
        path: String,
        project: Project,
        globalSearchScope: GlobalSearchScope
    ): List<Location<*>> {
        val location: Location<*>?
        println("protocol = $protocol")
        location = findConfigFile(path, project)
        return emptyList()
    }

    companion object {
        val INSTANCE = ChutneyTestLocationProvider()
        private fun findConfigFile(locationData: String, project: Project): Location<*>? {
            val virtualFile = LocalFileSystem.getInstance().findFileByIoFile(File(locationData))
            if (virtualFile != null && virtualFile.isValid) {
                val psiFile = PsiManager.getInstance(project).findFile(virtualFile)
                if (psiFile != null && psiFile.isValid) {
                    return PsiLocation.fromPsiElement(psiFile)
                }
            }
            return null
        }
    }
}

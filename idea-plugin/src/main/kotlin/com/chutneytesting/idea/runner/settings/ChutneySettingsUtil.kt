package com.chutneytesting.idea.runner.settings

import com.chutneytesting.idea.runner.TestType
import com.google.common.collect.Lists
import com.intellij.json.JsonFileType
import com.intellij.openapi.project.IndexNotReadyException
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Ref
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.search.GlobalSearchScopesCore
import java.io.File

object ChutneySettingsUtil {
    private val CONFIG_FILES_IN_PROJECT = Key<Boolean>("Chutney_SCENARIO_FILES_IN_PROJECT")
    fun collectChutneyConfigs(project: Project, runSettings: ChutneyRunSettings): List<VirtualFile?> {
        val testType = runSettings.testType
        var res: List<VirtualFile?> = emptyList<VirtualFile>()
        if (testType === TestType.ALL_SCENARIO_IN_DIRECTORY) {
            val virtualFile = VfsUtil.findFileByIoFile(File(runSettings.directory), true)
            if (virtualFile != null) {
                res = collectChutneyScenarioFilesInDirectory(project, virtualFile)
            }
        } else { //
        }
        return res
    }

    fun collectChutneyScenarioFilesInDirectory(project: Project, directory: VirtualFile): List<VirtualFile> {
        val directorySearchScope = buildDirectorySearchScope(project, directory) ?: return emptyList<VirtualFile>()
        val configs = FileTypeIndex.getFiles(JsonFileType.INSTANCE, directorySearchScope)
        return Lists.newArrayList(configs)
    }

    fun areChutneyConfigFilesInProjectCached(project: Project): Boolean {
        var value = project.getUserData(CONFIG_FILES_IN_PROJECT)
        if (value != null) {
            return value
        }
        value = areChutneyConfigFilesInScope(GlobalSearchScope.projectScope(project))
        if (value == null) {
            return false
        }
        project.putUserData(CONFIG_FILES_IN_PROJECT, value)
        return value
    }

    fun areChutneyConfigFilesInProject(project: Project): Boolean {
        val projectScope = GlobalSearchScope.projectScope(project)
        val found = areChutneyConfigFilesInScope(projectScope)
        return found ?: false
    }

    fun areChutneyConfigFilesInDirectory(project: Project, directory: VirtualFile): Boolean {
        val directorySearchScope = buildDirectorySearchScope(project, directory) ?: return false
        val found = areChutneyConfigFilesInScope(directorySearchScope)
        return found ?: false
    }

    private fun areChutneyConfigFilesInScope(scope: GlobalSearchScope): Boolean? {
        return try {
            val foundRef = Ref.create(false)
            FileTypeIndex.processFiles(JsonFileType.INSTANCE, { file: VirtualFile? ->
                /*if (JSLibraryUtil.isProbableLibraryFile(file)) {
          return true;
        }*/foundRef.set(true)
                false
            }, scope)
            foundRef.get()
        } catch (e: IndexNotReadyException) {
            null
        }
    }

    private fun buildDirectorySearchScope(project: Project, directory: VirtualFile): GlobalSearchScope? {
        val module = ProjectRootManager.getInstance(project).fileIndex.getModuleForFile(directory) ?: return null
        val directorySearchScope = GlobalSearchScopesCore.directoryScope(project, directory, true)
        return module.moduleContentWithDependenciesScope.intersectWith(directorySearchScope)
    }
}

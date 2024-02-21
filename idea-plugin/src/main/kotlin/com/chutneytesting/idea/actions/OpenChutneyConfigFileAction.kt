package com.chutneytesting.idea.actions

import com.chutneytesting.idea.logger.EventDataLogger
import com.intellij.ide.actions.RevealFileAction
import com.intellij.ide.actions.ShowFilePathAction
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.util.PathUtil
import java.io.File

class OpenChutneyConfigFileAction : AnAction() {

    private val LOG = Logger.getInstance(OpenChutneyConfigFileAction::class.java)

    override fun actionPerformed(event: AnActionEvent) {
        val project: Project = event.project ?: return
        val chutneyDirectoryConf = PathUtil.toSystemIndependentName(PathManager.getConfigPath() + "/chutney-idea-plugin/conf/")
        val confFile = chutneyDirectoryConf + "environment/" +"GLOBAL.json"
        val findFileByPath = LocalFileSystem.getInstance().findFileByPath(confFile)
        if (findFileByPath == null) {
            EventDataLogger.logWarning("Chutney ConfigFile <b>GLOBAL.json</b> can't be opened.<br>" +
                    "<a href=\"file\">Show In Explorer</a>", project
            ) { _, hyperlinkEvent ->
                if ("file" == hyperlinkEvent.description) {
                    //show file in explorer
                    val directory = File(chutneyDirectoryConf)
                    if (!directory.exists()) {
                        directory.mkdirs()
                    }
                    RevealFileAction.openFile(directory)
                }
            }
            return
        }
        FileEditorManager.getInstance(project).openFile(findFileByPath, true)
    }

}

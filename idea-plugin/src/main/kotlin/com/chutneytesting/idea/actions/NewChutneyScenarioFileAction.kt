package com.chutneytesting.idea.actions

import com.chutneytesting.idea.ChutneyFileType
import com.chutneytesting.idea.ChutneyIcons
import com.intellij.ide.actions.CreateFileFromTemplateAction
import com.intellij.ide.actions.CreateFileFromTemplateDialog
import com.intellij.ide.fileTemplates.FileTemplateManager
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory

const val Chutney_TEMPLATE_PREFIX = "Chutney "

class NewChutneyScenarioFileAction : CreateFileFromTemplateAction(
    "Chutney Scenario",
    "Creates new Chutney scenario", ChutneyFileType.ICON
) {

    override fun getActionName(directory: PsiDirectory, newName: String, templateName: String?): String {
        return "Chutney scenario"
    }

    override fun buildDialog(project: Project, directory: PsiDirectory, builder: CreateFileFromTemplateDialog.Builder) {
        builder.setTitle("Add Chutney scenario")
        val templates = FileTemplateManager.getDefaultInstance().allTemplates.filter {
            it.name.startsWith(Chutney_TEMPLATE_PREFIX)
        }
        for (template in templates) {
            builder.addKind(
                template.name.substring(Chutney_TEMPLATE_PREFIX.length),
                ChutneyIcons.ChutneyFile,
                template.name
            )
        }

    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.idea

import com.intellij.openapi.application.runReadAction
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.psi.PsiManager
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.SchemaType

class ChutneyJsonSchemaV2FileProvider(val project: Project) : JsonSchemaFileProvider {

    companion object {
        private val LOG = Logger.getInstance(ChutneyJsonSchemaV2FileProvider::class.java)
    }

    override fun getName(): String {
        return "Chutney V2"
    }

    override fun isAvailable(virtualFile: VirtualFile): Boolean {
        return runReadAction { isChutneyFile(virtualFile) }
    }

    private fun isChutneyFile(virtualFile: VirtualFile): Boolean {
        if (project.isDisposed) return false
        val psiFile = PsiManager.getInstance(project).findFile(virtualFile) ?: return false
        return ChutneyUtil.isChutneyJson(psiFile) || ChutneyUtil.isChutneyYaml(psiFile)
    }

    override fun getSchemaFile(): VirtualFile? {
        val resource = ChutneyJsonSchemaV2FileProvider::class.java.getResource("/chutney-v2.schema.json")
        val url = VfsUtil.convertFromUrl(resource!!)
        return VirtualFileManager.getInstance().findFileByUrl(url)
    }

    override fun getSchemaType(): SchemaType {
        return SchemaType.embeddedSchema
    }
}

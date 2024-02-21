package com.chutneytesting.idea

import com.intellij.openapi.project.Project
import com.jetbrains.jsonSchema.extension.JsonSchemaFileProvider
import com.jetbrains.jsonSchema.extension.JsonSchemaProviderFactory

class ChutneySchemaProviderFactory : JsonSchemaProviderFactory {

    override fun getProviders(project: Project): List<JsonSchemaFileProvider> {
        if (project.isDisposed) {
            return listOf()
        }
        return listOf(ChutneyJsonSchemaV1FileProvider(project), ChutneyJsonSchemaV2FileProvider(project))
    }
}

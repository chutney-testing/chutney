package com.chutneytesting.idea

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.intellij.json.psi.JsonFile
import com.intellij.json.psi.JsonObject
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.util.Computable
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import me.andrz.jackson.JsonReferenceProcessor
import org.jetbrains.annotations.ApiStatus
import org.jetbrains.kotlin.idea.util.findAnnotation
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.io.File

object ChutneyUtil {

    val mapper: ObjectMapper = ObjectMapper()
    val yamlReader = ObjectMapper(YAMLFactory())
    val jsonReferenceProcessor = JsonReferenceProcessor().apply {
        maxDepth = -1
        isPreserveRefs = true
        // isCacheInMemory = true
    }


    fun processJsonReference(jsonVirtualFile: VirtualFile): String {
        val node = jsonReferenceProcessor.process(File(jsonVirtualFile.path))
        return mapper.writeValueAsString(node)
    }

    @ApiStatus.ScheduledForRemoval
    @Deprecated("remove once all scenario are renamed from iceberg.json -> chutney.json")
    fun isLegacyScenarioJson(jsonPsi: PsiFile): Boolean {
        return jsonPsi.name.indexOf("iceberg.json") > 1
    }

    @ApiStatus.ScheduledForRemoval
    @Deprecated("remove once all scenario are renamed from iceberg.yaml -> chutney.yaml")
    fun isLegacyScenarioYaml(jsonPsi: PsiFile): Boolean {
        return jsonPsi.name.indexOf("iceberg.yaml") > 1 || jsonPsi.name.indexOf("iceberg.yml") > 1
    }

    fun isChutneyJson(jsonPsi: PsiFile): Boolean {
        return jsonPsi.name.indexOf("scenario.json") > 1 || jsonPsi.name.indexOf("chutney.json") > 1 || isLegacyScenarioJson(
            jsonPsi
        )
    }

    fun isChutneyYaml(jsonPsi: PsiFile): Boolean {
        return jsonPsi.name.indexOf("chutney.yml") > 1 || jsonPsi.name.indexOf("chutney.yaml") > 1 || isLegacyScenarioYaml(
            jsonPsi
        )
    }

    private fun isChutneyDsl(ktPsi: PsiFile?): Boolean {
        return (ktPsi?.name?.indexOf(".kt") ?: 0) > 1
    }

    fun isChutneyDslMethod(psiElement: PsiElement): Boolean {
        return isChutneyDsl(psiElement.containingFile) &&
                (psiElement is KtNamedFunction) &&
                hasAnnotation(psiElement, "KChutney")
    }

    private fun hasAnnotation(psiElement: KtNamedFunction, annotationName: String): Boolean {
        return psiElement.annotationEntries.find { it.shortName?.asString().equals(annotationName) } != null

    }

    fun isChutneyDsl(ktFile: VirtualFile): Boolean {
        return ktFile.name.indexOf(".kt") > 1
    }


    fun isIcefragJson(jsonPsi: PsiFile): Boolean {
        return jsonPsi.name.indexOf("icefrag.json") > 1
    }

    fun isChutneyJson(virtualFile: VirtualFile): Boolean {
        return virtualFile.name.indexOf("scenario.json") > 1 || virtualFile.name.indexOf("chutney.json") > 1
    }

    @ApiStatus.ScheduledForRemoval
    @Deprecated("remove once all scenario are renamed from iceberg.json -> chutney.json")
    fun isLegacyScenarioJson(virtualFile: VirtualFile): Boolean {
        return virtualFile.name.indexOf("iceberg.json") > 1
    }

    fun getChutneyScenarioIdFromFileName(fileName: String): Int? {
        val dashIndex = fileName.indexOf("-")
        return try {
            if (dashIndex > 0) Integer.valueOf(fileName.substring(0, dashIndex)) else null
        } catch (e: Exception) {
            null
        }
    }

    fun getChutneyScenarioDescriptionFromFileName(fileName: String): String {
        val description = fileName.substring(0, fileName.indexOf(".chutney.json"))
        if (description.contains("-")) {
            return description.substring(description.indexOf("-") + 1)
        }
        return description
    }

    fun isRemoteChutneyJson(jsonPsi: PsiFile): Boolean {
        return isChutneyJson(jsonPsi) && getChutneyScenarioIdFromFileName(jsonPsi.name) != null
    }

    fun isRemoteChutneyJson(jsonVF: VirtualFile): Boolean {
        return isChutneyJson(jsonVF) && getChutneyScenarioIdFromFileName(jsonVF.name) != null
    }

    fun isChutneyFragmentsJson(virtualFile: VirtualFile): Boolean {
        return virtualFile.name.indexOf("icefrag.json") > 1
    }

    fun isChutneyV1Json(jsonPsi: PsiFile): Boolean {
        if (jsonPsi !is JsonFile) return false
        return ApplicationManager.getApplication().runReadAction(Computable {
            val allTopLevelValues = jsonPsi.allTopLevelValues
            val first = allTopLevelValues.first { it is JsonObject }
            return@Computable isChutneyJson(jsonPsi) && (first as JsonObject).propertyList.any { it?.name == "scenario" }
        })
    }

    fun isChutneyV2Json(jsonPsi: PsiFile): Boolean {
        if (jsonPsi !is JsonFile) return false
        return ApplicationManager.getApplication().runReadAction(Computable {
            val allTopLevelValues = jsonPsi.allTopLevelValues
            val first = allTopLevelValues.first { it is JsonObject }
            return@Computable isChutneyJson(jsonPsi) && (first as JsonObject).propertyList.any {
                it?.name in listOf(
                    "givens",
                    "when",
                    "thens"
                )
            }
        })
    }

    fun convertYamlToJson(yaml: String): String {
        val obj = yamlReader.readValue(yaml, Any::class.java)
        return mapper.writeValueAsString(obj)
    }
}

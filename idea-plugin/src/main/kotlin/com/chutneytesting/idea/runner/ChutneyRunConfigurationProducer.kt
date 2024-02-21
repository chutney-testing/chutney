package com.chutneytesting.idea.runner

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.runner.settings.ChutneyRunSettings
import com.intellij.execution.actions.ConfigurationContext
import com.intellij.execution.actions.RunConfigurationProducer
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Ref
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiElement
import com.intellij.psi.util.parentOfType
import com.intellij.util.ObjectUtils
import org.jetbrains.kotlin.idea.refactoring.fqName.getKotlinFqName
import org.jetbrains.kotlin.psi.KtNamedFunction
import java.io.File
import java.util.*


class ChutneyRunConfigurationProducer :
    RunConfigurationProducer<ChutneyRunConfiguration>(ChutneyRunConfigurationType.getInstance()) {

    private fun logTakenTime(actionName: String, startTimeNano: Long, vararg args: String) {
        val NANO_IN_MS: Long = 1000000
        val durationNano = System.nanoTime() - startTimeNano
        if (durationNano > 100 * NANO_IN_MS) {
            val message = String.format(
                "[Chutney] Time taken by '$actionName': %.2f ms, extra args: %s\n",
                durationNano / (1.0 * NANO_IN_MS),
                Arrays.toString(args)
            )
            LOG.info(message)
        }
    }


    private fun buildRunSettingsContext(context: ConfigurationContext): ChutneyRunSettings? {
        if (context.containsMultipleSelection()) {
            val elements: Array<PsiElement>? = LangDataKeys.PSI_ELEMENT_ARRAY.getData(context.dataContext)
            if (elements != null) {
                val runSettings = findChutneyRunSettings(elements)
                if (runSettings != null) {
                    return runSettings
                }
            }
        } else {
            val location = context.location
            if (location != null) {
                val element = location.psiElement
                val runSettings = findChutneyRunSettings(element)
                if (runSettings != null) {
                    return runSettings
                }
            }

        }
        return null
    }

    private fun findChutneyRunSettings(element: PsiElement): ChutneyRunSettings? {
        for (ChutneyRunSettingsProvider in RUN_SETTINGS_PROVIDERS) {
            val runSettings = ChutneyRunSettingsProvider.provideSettings(element)
            if (runSettings != null) {
                return runSettings
            }
        }
        return null
    }

    private fun findChutneyRunSettings(elements: Array<PsiElement>): ChutneyRunSettings? {

        val files = elements
            .filter { it.containingFile != null }
            .filter { ChutneyUtil.isChutneyJson(it.containingFile) }
            .map { it.containingFile.virtualFile }
            .toList()

        if (files.size > 1) {
            return ChutneyRunSettings(
                scenariosFilesPath = files.joinToString(separator = ";") { getPath(it) },
                testType = TestType.MUTLI_SCENARIO_FILES
            )
        }
        return null

    }

    override fun isConfigurationFromContext(
        configuration: ChutneyRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val project = configuration.project
        val patternRunSettings = buildRunSettingsContext(context) ?: return false
        val candidateRunSettings = configuration.getRunSettings()
        val patternTestType = patternRunSettings.testType

        if (patternTestType === TestType.ALL_SCENARIO_IN_DIRECTORY) {
            val dir1 = File(patternRunSettings.directory)
            val dir2 = File(candidateRunSettings.directory)
            if (dir1.isDirectory && dir2.isDirectory && FileUtil.filesEqual(dir1, dir2)) {
                return true
            }
        }

        if (patternTestType === TestType.MUTLI_SCENARIO_FILES) {
            if ((patternRunSettings.scenariosFilesPath.split(";") -
                        candidateRunSettings.scenariosFilesPath.split(";"))
                    .isEmpty()
            ) {
                return true
            }
        }
        return false
    }

    private fun logDoneCreateConfigurationByElement(startTimeNano: Long, args: String) {
        logTakenTime("createConfigurationByElement", startTimeNano, args)
    }

    override fun setupConfigurationFromContext(
        configuration: ChutneyRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val project = configuration.project
        val original = context.getOriginalConfiguration(null)
        if (original != null && original.type !== ChutneyRunConfigurationType.getInstance()) {
            return false
        }
        val startTimeNano = System.nanoTime()
        val settings = buildRunSettingsContext(context)
        if (settings == null) {
            logDoneCreateConfigurationByElement(startTimeNano, "1")
            return false
        }

        configuration.setRunSettings(settings)

        val configurationName = configuration.resetGeneratedName()
        configuration.name = configurationName

        logDoneCreateConfigurationByElement(startTimeNano, "3")

        return true
    }

    class ChutneyFileRunSettingsProvider : ChutneyRunSettingsProvider {
        override fun provideSettings(psiElement: PsiElement): ChutneyRunSettings? {
            val psiFile = psiElement.containingFile ?: return null
            val virtualFile = psiFile.virtualFile
            if (virtualFile == null || (!ChutneyUtil.isChutneyJson(psiFile) && !ChutneyUtil.isChutneyYaml(psiFile) && !ChutneyUtil.isChutneyDslMethod(
                    psiElement.parent
                ))
            ) {
                return null
            }
            return ChutneyRunSettings(scenarioFilePath = getPath(virtualFile), testType = TestType.SCENARIO_FILE).apply {
                methodName = if (ChutneyUtil.isChutneyDslMethod(psiElement.parent))
                    getFullyQualifiedMethodName(psiElement)
                else ""
            }
        }

    }

    class ChutneyDirectoryRunSettingsProvider : ChutneyRunSettingsProvider {
        override fun provideSettings(psiElement: PsiElement): ChutneyRunSettings? {
            val psiDirectory = ObjectUtils.tryCast(psiElement, PsiDirectory::class.java) ?: return null
            val directory = psiDirectory.virtualFile
            return ChutneyRunSettings(directory = getPath(directory), testType = TestType.ALL_SCENARIO_IN_DIRECTORY)
        }

    }

    companion object {

        private val RUN_SETTINGS_PROVIDERS = arrayOf(
            ChutneyDirectoryRunSettingsProvider(),
            ChutneyFileRunSettingsProvider()
        )

        private val LOG = Logger.getInstance(ChutneyRunConfigurationProducer::class.java)

        fun getPath(virtualFile: VirtualFile): String {
            return FileUtil.toSystemDependentName(virtualFile.path)
        }
    }

}

fun getFullyQualifiedMethodName(psiElement: PsiElement): String {
    val methodName = psiElement.getKotlinFqName()?.asString() ?: error("cannot get fqName")
    if (methodName.contains(" ") && methodName.contains(".")) {
        return methodName.substringBeforeLast(".") + "." + "`${methodName.substringAfterLast(".")}`"
    }
    if (methodName.contains(" ")) {
        return "`$methodName`"
    }
    return methodName
}

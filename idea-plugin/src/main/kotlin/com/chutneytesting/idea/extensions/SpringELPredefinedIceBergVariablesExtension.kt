package com.chutneytesting.idea.extensions

import com.chutneytesting.idea.ChutneyFileType.Companion.ICON
import com.chutneytesting.idea.completion.JsonTraversal
import com.intellij.injected.editor.VirtualFileWindow
import com.intellij.json.psi.JsonProperty
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiVariable
import com.intellij.psi.impl.light.LightVariableBuilder
import com.intellij.spring.el.contextProviders.SpringElContextsExtension
import com.intellij.util.containers.ContainerUtil
import java.util.stream.Collectors
import java.util.stream.Stream

class SpringELPredefinedChutneyVariablesExtension : SpringElContextsExtension() {
    override fun getContextVariables(psiElement: PsiElement): MutableCollection<out PsiVariable?> {
        val variables = ContainerUtil.newSmartList<PsiVariable?>()
        var file = psiElement.containingFile.virtualFile
        if (file is VirtualFileWindow) {
            file = (file as VirtualFileWindow).delegate
        }
        val psiFile = PsiManager.getInstance(psiElement.project).findFile(file!!) ?: return variables
        val jsonTraversal = JsonTraversal()
        val variablesInCurrentFile: List<PsiElement> = jsonTraversal.getVariables(psiFile)
        val variablesInFrags: List<PsiElement> = jsonTraversal.getVariablesFromReferences(psiFile)
        val collect = Stream.concat(variablesInCurrentFile.stream(), variablesInFrags.stream())
            .map { tag: PsiElement? -> tag as JsonProperty? }
            .map<LightVariableBuilder<*>?> { e: JsonProperty? ->
                LightVariableBuilder<LightVariableBuilder<*>>(
                    e!!.name,
                    JavaPsiFacade.getInstance(psiElement.project).elementFactory.createTypeByFQClassName(
                        "java.lang.Object",
                        psiElement.resolveScope
                    ),
                    e.navigationElement
                )
            }
            .map { e: LightVariableBuilder<*>? -> e!!.setBaseIcon(ICON) }
            .collect(Collectors.toList())
        val date: LightVariableBuilder<*> = LightVariableBuilder<LightVariableBuilder<*>>(
            "date",
            JavaPsiFacade.getInstance(psiElement.project).elementFactory
                .createMethodFromText(
                    "public static java.time.temporal.Temporal date(String date, String format) {}",
                    null
                ).returnType!!, psiElement
        )
        val json: LightVariableBuilder<*> = LightVariableBuilder<LightVariableBuilder<*>>(
            "json",
            JavaPsiFacade.getInstance(psiElement.project).elementFactory
                .createMethodFromText(
                    "public static Object json(String document, String jsonPath) {}",
                    null
                ).returnType!!, psiElement
        )
        val xpath: LightVariableBuilder<*> = LightVariableBuilder<LightVariableBuilder<*>>(
            "xpath",
            JavaPsiFacade.getInstance(psiElement.project).elementFactory
                .createMethodFromText(
                    "public static Object xpath(String documentAsString, String xpath) {}",
                    null
                ).returnType!!, psiElement
        )
        val generate: LightVariableBuilder<*> = LightVariableBuilder<LightVariableBuilder<*>>(
            "generate",
            JavaPsiFacade.getInstance(psiElement.project).elementFactory
                .createMethodFromText("public static Generate generate() {}", null).returnType!!, psiElement
        )
        val payloads: LightVariableBuilder<*> = LightVariableBuilder<LightVariableBuilder<*>>(
            "payloads",
            JavaPsiFacade.getInstance(psiElement.project).elementFactory.createTypeByFQClassName("java.util.List"),
            psiElement
        )
        val body: LightVariableBuilder<*> = LightVariableBuilder<LightVariableBuilder<*>>(
            "body",
            JavaPsiFacade.getInstance(psiElement.project).elementFactory.createTypeByFQClassName("java.util.String"),
            psiElement
        )
        collect!!.add(date)
        collect.add(json)
        collect.add(xpath)
        collect.add(generate)
        collect.add(payloads)
        collect.add(body)
        return collect
    }
}

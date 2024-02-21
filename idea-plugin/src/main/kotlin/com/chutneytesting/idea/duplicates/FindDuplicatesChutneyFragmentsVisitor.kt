package com.chutneytesting.idea.duplicates

import com.chutneytesting.idea.ChutneyUtil
import com.google.common.collect.Maps
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.json.JsonFileType
import com.intellij.json.psi.JsonElementGenerator
import com.intellij.json.psi.JsonElementVisitor
import com.intellij.json.psi.JsonObject
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.CommandProcessor
import com.intellij.openapi.command.UndoConfirmationPolicy
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.ReadonlyStatusHandler
import com.intellij.psi.PsiManager
import com.intellij.psi.search.FileTypeIndex
import com.intellij.psi.search.GlobalSearchScope
import com.intellij.psi.util.PsiTreeUtil
import java.io.File
import java.io.IOException
import java.util.*


class FindDuplicatesChutneyFragmentsVisitor(private val holder: ProblemsHolder) : JsonElementVisitor() {

    override fun visitObject(o: JsonObject) {
        val gson = Gson()
        // get other Chutney files compare json nodes ...
        val list = FileTypeIndex.getFiles(JsonFileType.INSTANCE, GlobalSearchScope.projectScope(o.project))
            .filter { virtualFile -> ChutneyUtil.isChutneyJson(virtualFile) }
            .filter { virtualFile -> virtualFile.name != o.containingFile.virtualFile.name }

        list.forEach { file ->
            val findFile = PsiManager.getInstance(o.project).findFile(file)
            val findChildrenOfType = PsiTreeUtil.findChildrenOfType(findFile, JsonObject::class.java)
            findChildrenOfType.forEach { jsonObject ->
                if (jsonObject.text == o.text) {
                    // holder.registerProblem(o, String.format("Found duplicate: %s", file.presentableUrl), ProblemHighlightType.WARNING);
                }
            }
        }

        val icefrags = FileTypeIndex.getFiles(JsonFileType.INSTANCE, GlobalSearchScope.projectScope(o.project))
            .filter { virtualFile -> ChutneyUtil.isChutneyFragmentsJson(virtualFile) }
        icefrags.forEach { file ->
            val findFile = PsiManager.getInstance(o.project).findFile(file)
            val findChildrenOfType = PsiTreeUtil.findChildrenOfType(findFile, JsonObject::class.java)
            val fixes = ArrayList<LocalQuickFix>()
            val mapType = object : TypeToken<Map<String, Any>>() {}.type
            findChildrenOfType.forEach { jsonObject ->
                val firstMap: Map<String, Any> = gson.fromJson(o.text, mapType)
                val secondMap: Map<String, Any> = gson.fromJson(jsonObject.text, mapType)
                if (Maps.difference(firstMap, secondMap).areEqual()) {
                    fixes.add(UseFragmentsFix(jsonObject, o))
                    holder.registerProblem(
                        o,
                        String.format("Found duplicate: %s", file.presentableUrl),
                        UseFragmentsFix(jsonObject, o)
                    )
                }
            }
        }

    }
}

private class UseFragmentsFix(private var fragmentNode: JsonObject, private var duplicateNode: JsonObject) :
    LocalQuickFix {


    override fun getName(): String {
        return String.format("Replace json fragment with '%s' \$ref", fragmentNode.containingFile.name)
    }

    override fun applyFix(project: Project, descriptor: ProblemDescriptor) {

        //taken from
        ReadonlyStatusHandler.getInstance(project).ensureFilesWritable(duplicateNode.containingFile.virtualFile)

        ApplicationManager.getApplication().runWriteAction {
            CommandProcessor.getInstance().executeCommand(project, {
                try {
                    // file.delete(this)
                    // val jpath = "/steps"
                    val jpath = ""
                    val relative = File(duplicateNode.containingFile.virtualFile.parent.path).toURI()
                        .relativize(File(fragmentNode.containingFile.virtualFile.path).toURI())
                        .path
                    val relativePathFromDuplicateNodeToFragmentNode = "$relative#"
                    val replacementNode: JsonObject =
                        JsonElementGenerator(duplicateNode.project).createObject("\"\$ref\": \"$relativePathFromDuplicateNodeToFragmentNode$jpath\"")
                    duplicateNode.replace(replacementNode)

                } catch (e: IOException) {
                    //e.printStackTrace()
                }
            }, name, "???", UndoConfirmationPolicy.REQUEST_CONFIRMATION)
        }

    }

    override fun getFamilyName(): String {
        return name
    }
}


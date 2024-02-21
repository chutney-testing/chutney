package com.chutneytesting.idea.runner

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.module.ModuleUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.impl.FsRoot
import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineFactoryBase
import org.jetbrains.kotlin.cli.common.repl.ScriptArgsWithTypes
import org.jetbrains.kotlin.jsr223.KotlinJsr223JvmScriptEngine4Idea
import org.jetbrains.kotlin.script.jsr223.KotlinJsr223JvmLocalScriptEngine
import org.jetbrains.kotlin.script.util.scriptCompilationClasspathFromContextOrStlib
import java.io.File
import javax.script.ScriptContext
import javax.script.ScriptEngine
import kotlin.script.experimental.jvm.util.KotlinJars

class ChutneyKotlinJsr223JvmLocalScriptEngineFactory(val ktVirtualFile: VirtualFile, val project: Project) : KotlinJsr223JvmScriptEngineFactoryBase() {

    companion object{
        private val LOG = Logger.getInstance(ChutneyKotlinJsr223JvmLocalScriptEngineFactory::class.java)
    }

    override fun getScriptEngine(): ScriptEngine {
        val module = ModuleUtil.findModuleForFile(ktVirtualFile, project)
            ?: error("cannot find module")

        val classes = ModuleRootManager.getInstance(module).orderEntries().classes()
        val roots = classes
            .roots.filterIsInstance<FsRoot>()
            .map { it.path.substringBeforeLast('!') }
            .map { File(it) }
            .filter { it.exists() }


        val target = classes
            .roots.filter { it.isDirectory }
            .filter { it.path.lowercase().endsWith("classes") }
            .map { File(it.path) }
            .filter { it.exists() }

        LOG.info("dependencies for ${module.name}" + roots + classes)

        return KotlinJsr223JvmScriptEngine4Idea(
            this,
            scriptCompilationClasspathFromContextOrStlib(wholeClasspath = true) + KotlinJars.kotlinScriptStandardJars + roots + target,
            "kotlin.script.templates.standard.ScriptTemplateWithBindings",
            { ctx, argTypes ->
                ScriptArgsWithTypes(
                    arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)),
                    argTypes ?: emptyArray()
                )
            },
            arrayOf(Map::class)
        )
    }


}

package com.chutneytesting.kotlin.synchronize

import com.chutneytesting.kotlin.annotations.Scenario
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import io.github.classgraph.ClassGraph
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.kotlinFunction


/**
 * Synchronise scenarios annotated with [@Scenario](com.chutneytesting.kotlin.annotations.Scenario.kt) locally and/or remotely and returns elapsed time in milliseconds.
 */
fun synchronizeScenarios(
    packageName: String,
    serverInfo: ChutneyServerInfo? = null,
    updateRemote: Boolean = false,
    path: String = "src/main/chutney/generated",
    block: SynchronizeScenariosBuilder.() -> Unit = scenariosBuilder(packageName)
) {
    println("+-----------------------------------------------------------------------------------------------------------------------------------")
    val start = System.currentTimeMillis()
    val builder = SynchronizeScenariosBuilder()
    builder.block()
    builder.scenarios.forEach { it.synchronise(serverInfo = serverInfo, updateRemote = updateRemote, path = path) }
    val duration = System.currentTimeMillis() - start
    println("+-----------------------------------------------------------------------------------------------------------------------------------")
    println("| ATs json synchronized, please reformat code and push to your source code repository and update remote chutney ATs server. took $duration ms")
    println("+-----------------------------------------------------------------------------------------------------------------------------------")
}


/**
 * Synchronise scenario locally and/or remotely and returns elapsed time in milliseconds.
 */
fun ChutneyScenario.synchronise(
    serverInfo: ChutneyServerInfo? = null,
    updateRemote: Boolean = false,
    path: String = "src/main/chutney/generated"
) {
    var id = this.id
    if (updateRemote && serverInfo != null) {
        id = createOrUpdateRemoteScenario(serverInfo, this)
    }
    val scenario = this
    getJsonFile(path, scenario)?.apply {
        updateJsonFile(this, id, scenario)
    } ?: createJsonFile(path, id, scenario)
}

private fun scenariosBuilder(packageName: String): SynchronizeScenariosBuilder.() -> Unit = {
    getAllAnnotatedScenarios(packageName).forEach { scenario: KFunction<*> ->
        +scenario
    }
}

private fun getAllAnnotatedScenarios(packageName: String): List<KFunction<*>> {
    val annotationName = Scenario::class.java.canonicalName

    return ClassGraph()
        .enableAllInfo()
        .acceptPackages(packageName)
        .scan().use { scanResult ->
            scanResult.getClassesWithMethodAnnotation(annotationName).flatMap { routeClassInfo ->
                routeClassInfo.methodInfo.filter { function ->
                    function.hasAnnotation(Scenario::class.java)
                }.mapNotNull { method ->
                    method.loadClassAndGetMethod().kotlinFunction
                }
            }
        }
}

private fun updateJsonFile(file: File, id: Int?, scenario: ChutneyScenario) {
    file.writeText(scenario.toString())
    val fileNewName = getFileName(id, scenario.title)
    if (file.name != fileNewName) {
        renameJsonFile(file, fileNewName)
    }
    println("| AT json synchronized:: ${Path(file.parentFile.absolutePath).resolve(fileNewName)}")
}

private fun createJsonFile(pathCreated: String, id: Int?, scenario: ChutneyScenario) {
    File(pathCreated).mkdirs()
    File("$pathCreated/${getFileName(id, scenario.title)}")
        .apply { writeText(scenario.toString()) }
        .also { println("| AT json created:: ${it.absolutePath}") }
}

private fun getFileName(id: Int?, title: String) =
    (id?.let { id.toString() + "-" } ?: "") + title + ".chutney.json"

private fun getJsonFile(path: String, scenario: ChutneyScenario): File? {
    return File(path).walkTopDown().filter { it.isFile }
        .firstOrNull {
            val chutneyScenarioIdFromFileName = getChutneyScenarioIdFromFileName(it.name)
            val sameId = scenario.id != null && scenario.id == chutneyScenarioIdFromFileName
            val sameName = it.name.equals(getFileName(scenario.id, scenario.title), ignoreCase = true)
            sameId || sameName
        }
}

private fun createOrUpdateRemoteScenario(serverInfo: ChutneyServerInfo, scenario: ChutneyScenario): Int {
    try {
        val id = ChutneyServerServiceImpl.createOrUpdateJsonScenario(serverInfo, scenario)
        println("| remote AT json synchronized:: ${serverInfo.url}/#/scenario/${scenario.id}/executions?open=last&active=last")
        return id;
    } catch (e: Exception) {
        println("| remote AT with id: ${scenario.id} cannot be synchronized:: ${e.message}")
        throw e
    }
}


private fun renameJsonFile(
    jsonFile: File,
    fileName: String
) {
    try {
        Files.move(jsonFile.toPath(), jsonFile.toPath().resolveSibling(fileName))
    } catch (e: IOException) {
        println("| AT json file at ${jsonFile.name} cannot be renamed to: $fileName. ${e.message}")
    }
}

private fun getChutneyScenarioIdFromFileName(fileName: String): Int? {
    val dashIndex = fileName.indexOf("-")
    return try {
        if (dashIndex > 0) Integer.valueOf(fileName.substring(0, dashIndex)) else null
    } catch (e: Exception) {
        null
    }
}

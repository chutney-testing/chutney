package com.chutneytesting.kotlin.synchronize

import com.chutneytesting.kotlin.dsl.Campaign
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.Dataset
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import java.io.File
import java.io.IOException
import java.nio.file.Files
import kotlin.io.path.Path

/**
 * Serialize and Update if asked, Chutney scenarios.
 *
 * By default, scenarios are those returned by Scenario annotated functions found in given package name.
 * @see SynchronizeScenariosBuilder.scenariosBuilder
 *
 * @param packageName The package name to search for scenarios
 * @param serverInfo The remote server definition
 * @param updateRemote Activate the update onto the remote server
 * @param path The local path to serialize the found scenarios to
 * @param block The logic to find the scenarios.
 */
fun synchronizeScenarios(
    packageName: String,
    serverInfo: ChutneyServerInfo? = null,
    updateRemote: Boolean = false,
    path: String = "src/main/chutney/generated",
    block: SynchronizeScenariosBuilder.() -> Unit = SynchronizeScenariosBuilder.scenariosBuilder(packageName)
) {
    println("+-----------------------------------------------------------------------------------------------------------------------------------")
    val start = System.currentTimeMillis()
    val builder = SynchronizeScenariosBuilder()
    builder.block()
    builder.scenarios.forEach {
        if (updateRemote && serverInfo != null) {
            val synchronizedScenario = it.synchronise(serverInfo = serverInfo)
            synchronizedScenario.jsonSerialize(path)
        } else {
            it.jsonSerialize(path)
        }
    }
    val duration = System.currentTimeMillis() - start
    println("+-----------------------------------------------------------------------------------------------------------------------------------")
    println("| ATs json synchronized, please reformat code and push to your source code repository and update remote chutney ATs server. took $duration ms")
    println("+-----------------------------------------------------------------------------------------------------------------------------------")
}

/**
 * Synchronise scenario remotely
 */
fun ChutneyScenario.synchronise(serverInfo: ChutneyServerInfo): ChutneyScenario {
    try {
        val id = ChutneyServerServiceImpl.createOrUpdateJsonScenario(serverInfo, this)
        println("| remote AT json synchronized:: ${serverInfo.url}/#/scenario/$id/executions?open=last&active=last")
        return ChutneyScenario(id, title, description, defaultDataset, tags, givens, `when`, thens)
    } catch (e: Exception) {
        println("| remote AT with id: ${this.id} cannot be synchronized:: ${e.message}")
        throw e
    }
}

/**
 * Serialize scenario as JSON locally
 */
fun ChutneyScenario.jsonSerialize(path: String = "src/main/chutney/generated") =
    ChutneyScenarioJSONSerializer.jsonSerialize(path, this)

/**
 * Synchronise dataset remotely
 */
fun Dataset.synchronise(serverInfo: ChutneyServerInfo) {
    try {
        ChutneyServerServiceImpl.createOrUpdateDataset(serverInfo, this)
        println("| Dataset <$id> synchronized:: ${serverInfo.url}/#/dataset/$id/edition")
    } catch (e: Exception) {
        println("| Dataset <$id> cannot be synchronized:: ${e.message}")
        throw e
    }
}

/**
 * Synchronise campaign remotely
 */
fun Campaign.synchronise(serverInfo: ChutneyServerInfo) {
    try {
        ChutneyServerServiceImpl.createOrUpdateCampaign(serverInfo, this)
        println("| Campaign <$title> synchronized:: ${serverInfo.url}/#/campaign/$id/edition")
    } catch (e: Exception) {
        println("| Campaign <$title> cannot be synchronized:: ${e.message}")
        throw e
    }
}

private object ChutneyScenarioJSONSerializer {
    fun jsonSerialize(path: String, scenario: ChutneyScenario) {
        getJsonFile(path, scenario)?.apply {
            updateJsonFile(this, scenario)
        } ?: createJsonFile(path, scenario)
    }

    private fun getJsonFile(path: String, scenario: ChutneyScenario): File? {
        return File(path).walkTopDown().filter { it.isFile }
            .firstOrNull {
                val idAndNameFromFileName = getChutneyScenarioIdFromFileName(it.name)
                val sameId = scenario.id != null && scenario.id == idAndNameFromFileName.first
                val sameName = idAndNameFromFileName.second.equals(scenario.noIdFileName(), ignoreCase = true)
                sameId || sameName
            }
    }

    private fun getChutneyScenarioIdFromFileName(fileName: String): Pair<Int?, String> {
        val idAndName = fileName.split("-")
        return try {
            if (idAndName.size > 1) (Integer.valueOf(idAndName[0]) to idAndName[1]) else (null to idAndName[0])
        } catch (e: Exception) {
            null to ""
        }
    }

    private fun ChutneyScenario.fileName() = (id?.let { "$id-" } ?: "") + this.noIdFileName()
    private fun ChutneyScenario.noIdFileName() = "$title.chutney.json"

    private fun updateJsonFile(file: File, scenario: ChutneyScenario) {
        file.writeText(scenario.toString())
        val fileNewName = scenario.fileName()
        if (file.name != fileNewName) {
            renameJsonFile(file, fileNewName)
        }
        println("| AT json synchronized:: ${Path(file.parentFile.absolutePath).resolve(fileNewName)}")
    }

    private fun createJsonFile(pathCreated: String, scenario: ChutneyScenario) {
        File(pathCreated).mkdirs()
        File("$pathCreated/${scenario.fileName()}")
            .apply { writeText(scenario.toString()) }
            .also { println("| AT json created:: ${it.absolutePath}") }
    }

    private fun renameJsonFile(jsonFile: File, fileName: String) {
        try {
            Files.move(jsonFile.toPath(), jsonFile.toPath().resolveSibling(fileName))
        } catch (e: IOException) {
            println("| AT json file at ${jsonFile.name} cannot be renamed to: $fileName. ${e.message}")
        }
    }
}

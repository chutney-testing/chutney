package com.chutneytesting.kotlin.synchronize

import com.chutneytesting.environment.api.environment.dto.EnvironmentDto
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.Mapper
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.chutneytesting.kotlin.util.HttpClient
import com.chutneytesting.kotlin.util.HttpClientException
import org.apache.commons.text.StringEscapeUtils.escapeJson

interface ChutneyServerService {
    fun getAllScenarios(serverInfo: ChutneyServerInfo): List<LinkedHashMap<String, Any>>
    fun createOrUpdateJsonScenario(serverInfo: ChutneyServerInfo, scenario: ChutneyScenario): Int
    fun getEnvironments(serverInfo: ChutneyServerInfo): Set<EnvironmentDto>
}

object ChutneyServerServiceImpl : ChutneyServerService {

    override fun getAllScenarios(serverInfo: ChutneyServerInfo): List<LinkedHashMap<String, Any>> {
        return HttpClient.get(serverInfo, "/api/scenario/v2")
    }

    override fun createOrUpdateJsonScenario(serverInfo: ChutneyServerInfo, scenario: ChutneyScenario): Int {
        val remoteScenario: LinkedHashMap<String, Any>? = getRemoteScenarioById(serverInfo, scenario)
        return if (remoteScenario == null) {
            createJsonScenario(serverInfo, scenario)
        } else {
            updateJsonScenario(serverInfo, scenario, remoteScenario)
        }
    }

    private fun getRemoteScenarioById(
        serverInfo: ChutneyServerInfo,
        scenario: ChutneyScenario
    ): LinkedHashMap<String, Any>? {
        if (scenario.id == null) {
            return null;
        }
        return try {
            getRemoteScenario(serverInfo, scenario.id)
        } catch (exception: HttpClientException) {
            println("| could not find scenario with id : ${scenario.id} because $exception")
            null
        }
    }

    fun updateJsonScenario(
        serverInfo: ChutneyServerInfo,
        scenario: ChutneyScenario,
        remoteScenario: LinkedHashMap<String, Any>
    ): Int {
        var tags = emptyList<String>()
        val remoteTags = remoteScenario["tags"]
        if (remoteTags is List<*>) {
            tags = (scenario.tags + remoteTags.filterIsInstance<String>()).distinct()
        }
        val body = """
            {
                "id": "${scenario.id}" ,
                "content":"${escapeJson(scenario.toString())}",
                "title": "${scenario.title}",
                "description":"${scenario.description}" ,
                "tags": ${Mapper.toJson(tags)},
                "version": ${remoteScenario["version"]},
                ${buildDefaultDatasetJson(scenario)}
            }
        """.trimIndent()
        return try {
            HttpClient.post<Int>(serverInfo, "/api/scenario/v2/raw", body)
        } catch (e: HttpClientException) {
            println("| could not update scenario with id : ${scenario.id} because $e")
            -1
        }
    }

    private fun getRemoteScenario(
        serverInfo: ChutneyServerInfo,
        id: Int
    ): LinkedHashMap<String, Any> {
        return HttpClient.get(
            serverInfo, "/api/scenario/v2/raw/$id"
        )
    }

    private fun createJsonScenario(serverInfo: ChutneyServerInfo, scenario: ChutneyScenario): Int {
        val body = """
            {
                ${buildIdJson(scenario)}
                "content": "${escapeJson(scenario.toString())}",
                "title": "${scenario.title}",
                "description": "${scenario.description}",
                "tags": ${Mapper.toJson(scenario.tags)},
                ${buildDefaultDatasetJson(scenario)}
            }
        """.trimIndent()
        return try {
            HttpClient.post<Int>(serverInfo, "/api/scenario/v2/raw", body)
        } catch (e: HttpClientException) {
            println("| could not create scenario with id : ${scenario.id} because $e")
            -1
        }
    }

    private fun buildIdJson(scenario: ChutneyScenario): String {
        if (scenario.id != null) {
            return """
                "id": "${scenario.id}",
                """.trimIndent()
        }
        return "";
    }

    private fun buildDefaultDatasetJson(scenario: ChutneyScenario): String {
        if (scenario.defaultDataset != null) {
            return """
                "defaultDataset": "${scenario.defaultDataset}"
                """.trimIndent()
        }
        return """
                "defaultDataset": null
                """.trimIndent()
    }

    override fun getEnvironments(serverInfo: ChutneyServerInfo): Set<EnvironmentDto> {
        return HttpClient.get(serverInfo, "/api/v2/environment")
    }
}

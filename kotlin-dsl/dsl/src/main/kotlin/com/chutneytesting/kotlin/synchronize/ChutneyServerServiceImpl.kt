package com.chutneytesting.kotlin.synchronize

import com.chutneytesting.environment.api.environment.dto.EnvironmentDto
import com.chutneytesting.kotlin.dsl.Campaign
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.Dataset
import com.chutneytesting.kotlin.dsl.Mapper
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.chutneytesting.kotlin.util.HttpClient
import com.chutneytesting.kotlin.util.HttpClientException
import com.fasterxml.jackson.databind.ObjectMapper
import org.apache.commons.text.StringEscapeUtils.escapeJson
import java.util.*

interface ChutneyServerService {
    fun getAllScenarios(serverInfo: ChutneyServerInfo): List<LinkedHashMap<String, Any>>
    fun createOrUpdateJsonScenario(serverInfo: ChutneyServerInfo, scenario: ChutneyScenario): Int
    fun getEnvironments(serverInfo: ChutneyServerInfo): Set<EnvironmentDto>
    fun createOrUpdateDataset(serverInfo: ChutneyServerInfo, dataset: Dataset): String
    fun createOrUpdateCampaign(serverInfo: ChutneyServerInfo, campaign: Campaign): Int
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

    private fun updateJsonScenario(
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

    private const val chutneyDatasetEndpoint = "/api/v1/datasets"
    private fun Dataset.payload(): String {
        val om = ObjectMapper()
        return """
        {
          "id": "$id",
          "name": "$name",
          "description": "$description",
          "uniqueValues": ${om.writeValueAsString(uniqueValues)},
          "multipleValues": ${om.writeValueAsString(multipleValues)},
          "tags": ${om.writeValueAsString(tags)}
        }
    """.trimIndent()
    }

    override fun createOrUpdateDataset(serverInfo: ChutneyServerInfo, dataset: Dataset): String {
        val result = if (datasetIdExists(serverInfo, dataset.id)) {
            HttpClient.put<HashMap<String, Any>>(serverInfo, chutneyDatasetEndpoint, dataset.payload())
        } else {
            HttpClient.post<HashMap<String, Any>>(serverInfo, chutneyDatasetEndpoint, dataset.payload())
        }
        return result["id"] as String
    }

    private fun datasetIdExists(serverInfo: ChutneyServerInfo, id: String): Boolean {
        return try {
            Optional.ofNullable(
                HttpClient.get<HashMap<String, Any>>(serverInfo, "${chutneyDatasetEndpoint}/${id}")["id"]
            ).map { true }.orElseGet { false }
        } catch (e: Exception) {
            false
        }
    }

    private const val chutneyCampaignEndpoint = "/api/ui/campaign/v1"
    private fun Campaign.payload(): String {
        val om = ObjectMapper()
        return """
        {
          ${Optional.ofNullable(id).map { l -> "\"id\": $l," }.orElseGet { "" }}
          "title": "$title",
          "description": "$description",
          "scenarioIds": ${om.writeValueAsString(scenarioIds.map(Int::toString))},
          "scenarios": [${scenarios.joinToString(",") { it.payload() }}],
          "environment": "$environment",
          "parallelRun": $parallelRun,
          "retryAuto": $retryAuto,
          ${Optional.ofNullable(datasetId).map { d -> "\"datasetId\": \"$d\"," }.orElseGet { "" }}
          "tags": ${om.writeValueAsString(tags)}
        }
    """.trimIndent()
    }

    private fun Campaign.CampaignScenario.payload(): String {
        return """{"scenarioId": "$scenarioId"${datasetId?.let { id -> """, "datasetId": "$id" """ } ?: ""}}"""
    }

    override fun createOrUpdateCampaign(serverInfo: ChutneyServerInfo, campaign: Campaign): Int {
        val result = HttpClient.post<HashMap<String, Any>>(serverInfo, chutneyCampaignEndpoint, campaign.payload())
        return result["id"] as Int
    }
}

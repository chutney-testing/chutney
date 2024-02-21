package com.chutneytesting.kotlin.synchronize

import com.chutneytesting.kotlin.HttpTestBase
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.SuccessAction
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.http.RequestMethod
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder
import com.github.tomakehurst.wiremock.matching.UrlPattern
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString

class ScenarioSynchronizeServiceTest : HttpTestBase() {

    private val chutneyServerInfo = ChutneyServerInfo(url, "aUser", "aPassword")

    private val localScenario = Scenario(title = "A scenario") {
        When("Something happens") {
            SuccessAction()
        }
    }

    @Test
    fun should_create_new_scenario_local_file(@TempDir tempDir: Path) {

        // When & then
        assertScenarioSynchronization(tempDir = tempDir, scenario = localScenario)
        wireMockServer.verify(0, RequestPatternBuilder.allRequests());
}

    @Test
    fun should_update_scenario_local_file(@TempDir tempDir: Path) {

        // Given
        assertScenarioSynchronization(tempDir = tempDir, scenario = localScenario)

        val modifiedScenario = Scenario(title = "A scenario") {
            When("Something happens with success") {
                SuccessAction()
            }
        }

        // When & Then
        assertScenarioSynchronization(tempDir = tempDir, scenario = modifiedScenario)
        wireMockServer.verify(0, RequestPatternBuilder.allRequests());
    }

    @Test
    fun should_create_remote_scenario_and_rename_scenario_local_file(@TempDir tempDir: Path) {

        // Given
        assertScenarioSynchronization(tempDir = tempDir, scenario = localScenario)

        val modifiedScenario = Scenario(title = "A scenario") {
            When("Something happens with success") {
                SuccessAction()
            }
        }

        val createdScenarioId = 1
        wireMockServer.stubFor(
            WireMock.post(WireMock.urlEqualTo("/api/scenario/v2/raw"))
                .willReturn(
                    WireMock.aResponse()
                        .withBody(createdScenarioId.toString())
                )
        )

        // When & Then
        assertScenarioSynchronization(
            tempDir = tempDir,
            scenario = modifiedScenario,
            id = createdScenarioId,
            updateRemote = true
        )
        wireMockServer.verify(RequestPatternBuilder.newRequestPattern(
            RequestMethod.POST, UrlPattern.fromOneOf(null, null, "/api/scenario/v2/raw", null, null)))
        val requestJson = wireMockServer.allServeEvents.filter {
            it.request.url == "/api/scenario/v2/raw" && it.request.method.value() == "POST"
        }.map { it.request.bodyAsString }
        assertThat(requestJson.size).isEqualTo(1)
        assertThat(requestJson.first()).contains(localScenario.title)
        assertThat(requestJson.first()).contains("Something happens with success")
        assertThat(requestJson.first()).contains("KOTLIN")
    }

    @Test
    fun should_update_scenario_remotely_and_locally(@TempDir tempDir: Path) {
        // Given
        var existingScenario = Scenario(id = 1, title = "A scenario") {
            When("Something happens with success") {
                SuccessAction()
            }
        }
        assertScenarioSynchronization(
            tempDir = tempDir,
            scenario = existingScenario,
            id = existingScenario.id,
            updateRemote = false
        )

        existingScenario = Scenario(id = 1, title = "An other scenario title") {
            When("Something happens with success") {
                SuccessAction()
            }
        }

        wireMockServer.stubFor(
            WireMock.get(WireMock.urlEqualTo("/api/scenario/v2/raw/1"))
                .willReturn(
                    WireMock.aResponse()
                        .withBody("""{"version": 1,"tags": ["TEST"]}""")
                )
        )
        wireMockServer.stubFor(
            WireMock.post(WireMock.urlEqualTo("/api/scenario/v2/raw"))
                .willReturn(
                    WireMock.aResponse()
                        .withBody("1")
                )
        )

        // When & Then
        assertScenarioSynchronization(
            tempDir = tempDir,
            scenario = existingScenario,
            id = existingScenario.id,
            updateRemote = true
        )
        wireMockServer.verify(1, WireMock.getRequestedFor(WireMock.urlEqualTo("/api/scenario/v2/raw/1")))

        val requestJson = wireMockServer.allServeEvents.filter {
            it.request.url == "/api/scenario/v2/raw" && it.request.method.value() == "POST"
        }.map { it.request.bodyAsString }
        assertThat(requestJson.size).isEqualTo(1)
        assertThat(requestJson.first()).contains(existingScenario.title)
        assertThat(requestJson.first()).contains(existingScenario.id.toString())
        assertThat(requestJson.first()).contains("Something happens with success")
        assertThat(requestJson.first()).contains("TEST")
        assertThat(requestJson.first()).contains("KOTLIN")
    }

    @Test
    fun should_create_scenario_with_explicit_id() {

        // Given
        val scenario = ChutneyScenario(123, "title", "description")
        val expectedBodyRequest  = mapOf("content" to "{" + System.lineSeparator() + "  \"title\": \"title\"," + System.lineSeparator() + "  \"description\": \"description\"" + System.lineSeparator() + "}" + System.lineSeparator(),
                "id" to "123",
                "title" to "title",
                "description" to "description",
                "tags" to listOf("KOTLIN"))
        wireMockServer.stubFor(
            WireMock.post(WireMock.urlEqualTo("/api/scenario/v2/raw")).withRequestBody(WireMock.equalToJson(JSONObject(expectedBodyRequest ).toString()))
                .willReturn(
                    WireMock.aResponse()
                        .withBody("123")
                )
        )
        wireMockServer.stubFor(
            WireMock.post(WireMock.urlEqualTo("/api/scenario/v2/raw/123"))
                .willReturn(
                    WireMock.aResponse().withStatus(404)
                )
        )
        val chutneyServerInfo = ChutneyServerInfo(
                url,
                "aUser",
                "aPassword"
        )

        // When
        ChutneyServerServiceImpl.createOrUpdateJsonScenario(chutneyServerInfo, scenario)

        // Then
        Assertions.assertThat(wireMockServer.allServeEvents.filter {
            it.request.url == "/api/scenario/v2/raw" && it.request.method.value() == "POST"
        }.toList().size).isEqualTo(1)
    }

    private fun assertScenarioSynchronization(
        tempDir: Path,
        scenario: ChutneyScenario,
        id: Int? = null,
        updateRemote: Boolean = false
    ) {
        // When
        scenario.synchronise(
            serverInfo = chutneyServerInfo,
            path = tempDir.absolutePathString(),
            updateRemote = updateRemote
        )

        // Then
        val tmpDirFiles = File(tempDir.absolutePathString()).walkTopDown().filter { it.isFile }
        assertThat(tmpDirFiles.count()).isEqualTo(1)
        val jsonFile = tmpDirFiles.first()
        val fileName = (id?.let { "$id-" } ?: "") + scenario.title + ".chutney.json"
        assertThat(jsonFile.name).isEqualTo(fileName)
        assertThat(jsonFile.readText()).isEqualTo(scenario.toString())
    }
}

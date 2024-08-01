package blackbox

import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.chutneytesting.kotlin.util.HttpClient
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.InstanceOfAssertFactories
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.BindMode
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Testcontainers
import util.WSLUtil
import java.io.File
import java.nio.file.Files
import java.time.Duration

@Testcontainers
class IntegrationTest {

    companion object {
        private var chutneyServer: GenericContainer<Nothing>? = null
        var adminServerInfo: ChutneyServerInfo? = null
        var userServerInfo: ChutneyServerInfo? = null

        @JvmStatic
        @BeforeAll
        fun setUp() {
            val tempDirectory = Files.createTempDirectory("chutney-kotlin-blackbox-")
            val memAuthConfigFile = File(
                IntegrationTest::class.java.getResource("/blackbox/application-mem-auth.yml")!!.path
            )

            // Copy mem-auth config
            Files.copy(memAuthConfigFile.toPath(), tempDirectory.resolve("application-mem-auth.yml"))

            // Start server
            chutneyServer = GenericContainer<Nothing>("ghcr.io/chutney-testing/chutney/chutney-server:latest")
                .apply {
                    withStartupTimeout(Duration.ofSeconds(80))
                    withExposedPorts(8443)
                    withFileSystemBind(WSLUtil.wslPath(tempDirectory), "/config", BindMode.READ_WRITE)
                }
            chutneyServer!!.start()
            adminServerInfo =
                ChutneyServerInfo(
                    "https://${chutneyServer?.host}:${chutneyServer?.firstMappedPort}",
                    "admin",
                    "admin",
                    null,
                    null,
                    null
                )
            userServerInfo =
                ChutneyServerInfo(
                    "https://${chutneyServer?.host}:${chutneyServer?.firstMappedPort}",
                    "user",
                    "user",
                    null,
                    null,
                    null
                )

            // Set authorizations
            val roles = IntegrationTest::class.java.getResource("/blackbox/roles.json")!!.path
            HttpClient.post<Any>(adminServerInfo!!, "/api/v1/authorizations", File(roles).readText())
        }

        @JvmStatic
        @AfterAll
        fun cleanUp() {
            chutneyServer?.stop()
        }
    }

    @Test
    fun create_update_scenario_with_specific_ids() {
        // Given
        var body = """
            {
                "id": "1234",
                "title": "My scenario",
                "content": "{\"when\": {}}",
                "description": "My scenario description",
                "tags": [],
                "defaultDataset": null
            }
        """.trimIndent()

        // Create scenario
        var result = HttpClient.post<String>(adminServerInfo!!, "/api/scenario/v2/raw", body)
        assertThat(result).isEqualTo("1234")

        // Update scenario
        body = """
            {
                "id": "1234",
                "title": "My new title",
                "content": "{\"when\": {}}",
                "description": "My new scenario description",
                "tags": ["A_TAG"],
                "defaultDataset": null,
                "version": 1
            }
        """.trimIndent()
        result = HttpClient.post<String>(adminServerInfo!!, "/api/scenario/v2/raw", body)
        assertThat(result).isEqualTo("1234")

        // Then
        val resultRaw = HttpClient.get<Map<String, Any>>(userServerInfo!!, "/api/scenario/v2/raw/1234")
        assertThat(resultRaw)
            .containsEntry("id", "1234")
            .containsEntry("title", "My new title")
            .containsEntry("description", "My new scenario description")
            .containsEntry("tags", listOf("A_TAG"))
    }

    @Test
    fun create_update_campaign_with_specific_ids() {
        // Given
        val scenarioIdA = createEmptyScenario()
        val scenarioIdB = createEmptyScenario()

        // Create campaign
        var body = """
            {
                "id": 1234,
                "title": "My campaign",
                "description": "",
                "scenarios": [{"scenarioId": "$scenarioIdA"}],
                "environment": "DEFAULT",
                "parallelRun": false,
                "retryAuto": false,
                "datasetId": null,
                "tags": []
            }
        """.trimIndent()
        var result = HttpClient.post<HashMap<String, Any>>(adminServerInfo!!, "/api/ui/campaign/v1", body)
        assertThat(result)
            .containsEntry("id", 1234)
            .extractingByKey("scenarios").asInstanceOf(InstanceOfAssertFactories.LIST)
            .first(InstanceOfAssertFactories.MAP).containsEntry("scenarioId", scenarioIdA)

        // Update campaign
        body = """
            {
                "id": 1234,
                "title": "My new campaign",
                "description": "My new campaign description",
                "scenarios": [{"scenarioId": "$scenarioIdB"}],
                "environment": "DEFAULT",
                "parallelRun": false,
                "retryAuto": false,
                "datasetId": null,
                "tags": ["A_TAG"]
            }
        """.trimIndent()
        result = HttpClient.post<HashMap<String, Any>>(adminServerInfo!!, "/api/ui/campaign/v1", body)
        assertThat(result)
            .containsEntry("id", 1234)
            .extractingByKey("scenarios").asInstanceOf(InstanceOfAssertFactories.LIST)
            .first(InstanceOfAssertFactories.MAP).containsEntry("scenarioId", scenarioIdB)

        // Then
        val resultRaw = HttpClient.get<Map<String, Any>>(userServerInfo!!, "/api/ui/campaign/v1/1234")
        assertThat(resultRaw)
            .containsEntry("id", 1234)
            .containsEntry("title", "My new campaign")
            .containsEntry("description", "My new campaign description")
            .containsEntry("tags", listOf("A_TAG"))
            .extractingByKey("scenarios").asInstanceOf(InstanceOfAssertFactories.LIST)
            .first(InstanceOfAssertFactories.MAP).containsEntry("scenarioId", scenarioIdB)
    }

    private fun createEmptyScenario(): String {
        val body = """
            {
                "title": "Empty scenario",
                "content": "{\"when\": {}}",
                "tags": []
            }
        """.trimIndent()
        return HttpClient.post<String>(adminServerInfo!!, "/api/scenario/v2/raw", body)
    }
}

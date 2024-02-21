package com.chutneytesting.kotlin.synchronize

import EnvironmentSynchronizeService
import com.chutneytesting.environment.domain.Target
import com.chutneytesting.environment.infra.JsonFilesEnvironmentRepository
import com.chutneytesting.kotlin.HttpTestBase
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.github.tomakehurst.wiremock.client.WireMock.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path

class EnvironmentServiceSynchronizationTest : HttpTestBase() {

    @Test
    fun should_synchronize_local_environments_from_remote(@TempDir tempDir: Path) {
        // Given
        val environmentsFilesPath = tempDir.toAbsolutePath().toString()
        val envsResponse = """[{
                        "name": "CHUTNEY",
                        "description": "desc",
                        "targets": [
                            {
                                "name": "db",
                                "url": "dbUrl",
                                "properties": [
                                    {
                                        "key": "driverClassName",
                                        "value": "oracle.jdbc.OracleDriver"
                                    }
                                ]
                            }
                        ]
                    }
                    ]
                    """.trimIndent()
        stubFor(
            get(urlEqualTo("/api/v2/environment"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(envsResponse)
                )
        )
        val chutneyServerInfo = ChutneyServerInfo(
            url,
            "aUser",
            "aPassword"
        )

        val environmentSynchronizeService = EnvironmentSynchronizeService()

        // When
        environmentSynchronizeService.synchroniseLocal(
            serverInfo = chutneyServerInfo,
            environmentsPath = environmentsFilesPath,
            force = true
        )

        // Then
        val jsonFilesEnvironmentRepository = JsonFilesEnvironmentRepository(environmentsFilesPath)
        val actualEnvironment = jsonFilesEnvironmentRepository.findByName("CHUTNEY")
        Assertions.assertThat(actualEnvironment.name).isEqualTo("CHUTNEY")
        Assertions.assertThat(actualEnvironment.description).isEqualTo("desc")
        Assertions.assertThat(actualEnvironment.targets).hasSize(1)
        Assertions.assertThat(actualEnvironment.targets.iterator().next()).isEqualTo(
            Target.builder().withName("db")
                .withUrl("dbUrl")
                .withEnvironment("CHUTNEY")
                .withProperty("driverClassName", "oracle.jdbc.OracleDriver")
                .build()
        )
    }
}

package com.chutneytesting.example.http

import com.chutneytesting.example.scenario.HTTP_TARGET_NAME
import com.chutneytesting.example.scenario.http_scenario
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.Environment
import com.chutneytesting.kotlin.launcher.Launcher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import org.wiremock.integrations.testcontainers.WireMockContainer

@Testcontainers
class HttpScenarioTest {

    private var httpAddress: String = ""
    private var httpPort: Int = 0
    private var environment: ChutneyEnvironment = ChutneyEnvironment("default value")

    @Container
    private val wireMockContainer = WireMockContainer(DockerImageName.parse("wiremock/wiremock:2.35.0"))
        .withMappingFromResource("example/wiremock-testcontainer/create-&-update-film/post_film.json")
        .withMappingFromResource("example/wiremock-testcontainer/create-&-update-film/patch_film.json")
        .withMappingFromResource("example/wiremock-testcontainer/create-&-update-film/get_film.json")

    @BeforeEach
    fun setUp() {
        httpAddress = "localhost"
        httpPort = wireMockContainer.firstMappedPort
        environment = Environment(name = "local", description = "local environment") {
            Target {
                Name(HTTP_TARGET_NAME)
                Url("http://$httpAddress:${wireMockContainer.port}")
            }
        }
    }

    @Test
    fun `create & update film` () {
        Launcher().run(http_scenario, environment)
    }
}

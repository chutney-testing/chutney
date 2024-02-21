package com.chutneytesting.example.jms

import com.chutneytesting.example.scenario.ACTIVEMQ_TARGET_NAME
import com.chutneytesting.example.scenario.FILMS_DESTINATION
import com.chutneytesting.example.scenario.activemq_scenario
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.Environment
import com.chutneytesting.kotlin.launcher.Launcher
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@Testcontainers
class JmsScenarioTest {

    private var activemqAddress: String = ""
    private var hostJmsPort = 61616
    private var containerJmsPort = 61616
    private var environment: ChutneyEnvironment = ChutneyEnvironment("default value")


    @Container
    val activemqContainer = GenericContainer(DockerImageName.parse("webcenter/activemq"))
        .withExposedPorts(containerJmsPort)

    @BeforeEach
    fun setUp() {
        activemqAddress = activemqContainer.host
        hostJmsPort = activemqContainer.getMappedPort(containerJmsPort)
        environment = Environment(name = "local", description = "local environment") {
            Target {
                Name(ACTIVEMQ_TARGET_NAME)
                Url("tcp://$activemqAddress:$hostJmsPort")
                Properties("java.naming.factory.initial" to "org.apache.activemq.jndi.ActiveMQInitialContextFactory",
                    "jndi.queue.$FILMS_DESTINATION" to FILMS_DESTINATION
             )
            }
        }
    }

    @Test
    fun `publish & consume jms message`() {
        Launcher().run(activemq_scenario, environment)
    }
}

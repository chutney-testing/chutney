/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance

import com.chutneytesting.acceptance.tests.*
import com.chutneytesting.acceptance.tests.actions.*
import com.chutneytesting.acceptance.tests.edition.*
import com.chutneytesting.acceptance.tests.engine.*
import com.chutneytesting.environment.api.environment.dto.EnvironmentDto
import com.chutneytesting.environment.api.target.dto.TargetDto
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.ChutneyTarget
import com.chutneytesting.kotlin.launcher.Launcher
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.chutneytesting.kotlin.util.HttpClient
import com.chutneytesting.tools.Entry
import com.chutneytesting.tools.SocketUtils
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.paranamer.ParanamerModule
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.*
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.utility.MountableFile
import java.io.File
import java.time.Duration

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AcceptanceTests {

  private val om = jacksonObjectMapper()
    .registerModule(JavaTimeModule())
    .registerModule(ParanamerModule())

  private var chutneyServer: GenericContainer<Nothing>? = null
  private var environment: ChutneyEnvironment = ChutneyEnvironment("DEFAULT")
  private var chutneyServerInfo: ChutneyServerInfo? = null

  private var actionHttpPort: Int? = null
  private var actionAmqpPort: Int? = null
  private var actionJakartaPort: Int? = null

  @BeforeAll
  fun setUp() {
    // Start system under test : Chutney server + test env //
    val chutneyNetwork = Network.builder()
      .createNetworkCmdModifier {
        it.withName("chutney_network")
      }
      .build()
    chutneyServer = GenericContainer<Nothing>("ghcr.io/chutney-testing/chutney/chutney-server").apply {
      waitingFor(Wait.forLogMessage(".*Started ServerBootstrap.*", 1))
      withStartupTimeout(Duration.ofSeconds(80))
      withExposedPorts(8443)
      withNetwork(chutneyNetwork)
      withAccessToHost(true)
      withCopyFileToContainer(
        MountableFile.forClasspathResource("/blackbox/"),
        "/config"
      )
      withEnv("SPRING_CONFIG_LOCATION", "file:/config/")
    }
    chutneyServer!!.start()

    actionHttpPort = SocketUtils.freePortFromSystem()
    actionAmqpPort = SocketUtils.freePortFromSystem()
    System.setProperty("qpid.amqp_port", actionAmqpPort.toString())
    actionJakartaPort = SocketUtils.freePortFromSystem()
    org.testcontainers.Testcontainers.exposeHostPorts(
      actionHttpPort!!, actionAmqpPort!!, actionJakartaPort!!
    )

    // Setup system under test : Chutney server //
    val chutneyServerHost = chutneyServer!!.host
    val chutneyServerPort = chutneyServer!!.getMappedPort(8443)
    chutneyServerInfo = ChutneyServerInfo(
      "https://$chutneyServerHost:$chutneyServerPort", "admin", "admin",
      null, null, null
    )

    chutneyServerInfo?.let {
      // Authorizations
      val roles = AcceptanceTests::class.java.getResource("/blackbox/roles.json")!!.path
      HttpClient.post<Any>(it, "/api/v1/authorizations", File(roles).readText())
    }

    // Build launcher test environment //
    environment = ChutneyEnvironment(
      name = environment.name, targets =
        listOf(
          ChutneyTarget(
            "CHUTNEY_LOCAL",
            "https://$chutneyServerHost:$chutneyServerPort",
            mapOf("username" to "admin", "password" to "admin")
          ),
          ChutneyTarget("CHUTNEY_LOCAL_NO_USER", "https://$chutneyServerHost:$chutneyServerPort", emptyMap())
        )
    )
  }

  @AfterAll
  fun cleanUp() {
    chutneyServer?.stop()
  }

  @Test
  fun `Execution by campaign id with 2 scenarios`() {
    softlyAssertLauncherRun(
      listOf(
        executeCampaignById,
        executeCampaignByName,
        executeForSurefireReport,
        unknownCampaignById,
        unknownCampaignByName
      )
    )
  }

  @Test
  fun `Support testcase edition metadata`() {
    softlyAssertLauncherRun(
      listOf(
        readTestCaseMetadataScenario,
        readTestCaseAfterUpdateScenario,
        updateTestCaseWithBadVersionScenario
      )
    )
  }

  @Test
  fun `Support testcase editions`() {
    softlyAssertLauncherRun(
      listOf(
        `Request testcase edition`,
        `Request for a second time testcase edition`,
        `End testcase edition`,
        `Edition time to live`
      )
    )
  }

  @Test
  fun `SQL Task test`() {
    softlyAssertLauncherRun(
      listOf(
        `Sql query success`,
        `Sql query wrong table`
      )
    )
  }

  @Test
  fun `Success feature`() {
    softlyAssertLauncherRun(
      listOf(
        `Direct Success`,
        `Substeps Success`
      )
    )
  }

  @Test
  fun `Amqp feature`() {
    Launcher().run(`amqp test all steps`(actionAmqpPort!!), environment)
  }

  @Test
  fun `Kafka all Tasks test`() {
    softlyAssertLauncherRun(
      listOf(
        `Kafka basic publish wrong url failure`,
        `Kafka basic publish success`
      )
    )
  }

  @Test
  fun `Roles declarations and users associations`() {
    softlyAssertLauncherRun(
      listOf(
        `Declare a new role with its authorizations`,
        `Add and remove user to-from an existing role`
      )
    )
  }

  @Test
  fun `Execution success action`() {
    softlyAssertLauncherRun(
      listOf(
        `Action instantiation and execution of a success scenario`,
        `Task instantiation and execution of a failed scenario`,
        `Task instantiation and execution of a sleep scenario`,
        `Task instantiation and execution of a debug scenario`
      )
    )
  }

  @Test
  fun `Finally actions`() {
    Launcher().run(`Step of a type self registering as Finally Action does not create an infinite loop`, environment)
  }

  @Test
  fun `Execution with jsonPath function`() {
    softlyAssertLauncherRun(
      listOf(
        `Scenario execution with simple json value extraction`,
        `Scenario execution with multiple json value extraction`,
        `Scenario execution with json object value extraction`
      )
    )
  }

  @Test
  fun `Execution retry strategy`() {
    Launcher().run(`Retry should stop after success assertion`, environment)
  }

  @Test
  fun `Replace scenario parameters with data set or global var values`() {
    Launcher().run(`Execute gwt scenario with global vars`, environment)
  }

  @Test
  fun `Engine actions exposition`() {
    listOf(
      Pair("debug", "[[{\"name\": \"filters\",\"type\": \"java.util.List\"}]]"),
      Pair("assert", "[[{\"name\": \"asserts\",\"type\": \"java.util.List\"}]]"),
      Pair(
        "compare",
        "[[{\"name\": \"actual\",\"type\": \"java.lang.String\"},{\"name\": \"expected\",\"type\": \"java.lang.String\"},{\"name\": \"mode\",\"type\": \"java.lang.String\"}]]"
      )
    ).forEach {
      Launcher().run(`Retrieve action action-id from all actions and by identifier`(it.first, it.second), environment)
    }

  }

  @Test
  fun `HTTP server Task test`() {
    Launcher().run(`Http post request local server endpoint`, environment)
  }

  @Test
  fun `HTTP client Task test`() {
    listOf(
      Pair("GET", "uri: /notused"),
      Pair("DELETE", "uri: /notused"),
      Pair("POST", "uri: /notused  body: cool buddy"),
      Pair("PUT", "uri: /notused body: cool buddy"),
    ).forEach {
      Launcher().run(`Http (verb) request wrong url`(it.first, it.second), environment)
    }

    listOf(
      Pair("GET", "uri: /mock/get"),
      Pair("DELETE", "uri: /mock/delete"),
      Pair("POST", "uri: /mock/post  body: cool buddy"),
      Pair("PUT", "uri: /mock/put body: cool buddy"),
    ).forEach {
      Launcher().run(`Http (verb) request local valid endpoint`(it.first, it.second, actionHttpPort!!), environment)
    }
  }

  @Test
  fun `Micrometer Tasks test`() {
    softlyAssertLauncherRun(
      listOf(
        `Micrometer counter meter`,
        `Micrometer timer meter`,
        `Micrometer timer meter with start and stop`,
        `Micrometer gauge meter`,
        `Micrometer distribution summary meter`
      )
    )
  }

  @Test
  fun `Assertions Task test`() {
    softlyAssertLauncherRun(
      listOf(
        `Execution by UI controller`,
        `All in one assertions`,
        `Test xsd actions`
      )
    )
  }

  @Test
  fun `Final action for registering final actions for a testcase`() {
    softlyAssertLauncherRun(
      listOf(
        `Register simple success action`,
        `Register multiple actions with one complex, ie with inputs and strategy`,
        `Register final action with validations on outputs`
      )
    )
  }

  @Test
  fun `Jms Task test`() {
    Launcher().run(`Jms sender then clean then send and listen it on embedded broker`(), environment)
    listOf(
      Pair("sender", "destination: test \\n body: something"),
      Pair("clean-queue", "destination: test \\n bodySelector: selector"),
      Pair("listener", "destination: test \\n bodySelector: selector")
    ).forEach {
      Launcher().run(`Jms jmsAction wrong url`(it.first, it.second), environment)
    }
  }

  @Test
  fun `Jakarta Task test`() {
    Launcher().run(
      `Jakarta sender then clean then send and listen it on embedded broker`(actionJakartaPort!!),
      environment
    )
    listOf(
      Pair("sender", "destination: test \\n body: something"),
      Pair("clean", "destination: test \\n bodySelector: selector"),
      Pair("listener", "destination: test \\n bodySelector: selector")
    ).forEach {
      Launcher().run(`Jakarta actionInputs wrong url`(it.first, it.second), environment)
    }
  }

  @Nested
  @TestInstance(TestInstance.Lifecycle.PER_CLASS)
  inner class SSHTests {
    private var sshContainer: ComposeContainer? = null
    private val jumpServerUrl = "ssh://jump-host"
    private val sshEnv = EnvironmentDto(
      "SSH_ENV", "The SSH tests environment", listOf(
        TargetDto(
          "SSH_JUMP_SERVER", jumpServerUrl,
          setOf(
            Entry("user", "jumpuser"),
            Entry("privateKey", "/config/env/ssh/client-jump-id_ecdsa.key")
          )
        ),
        TargetDto(
          "SSH_INTERN_SERVER", "ssh://intern-host",
          setOf(
            Entry("user", "internuser"),
            Entry("privateKey", "/config/env/ssh/client-intern-id_edcsa.key"),
            Entry("proxy", jumpServerUrl),
            Entry("proxyUser", "jumpuser"),
            Entry("proxyPrivateKey", "/config/env/ssh/client-jump-id_ecdsa.key")
          )
        ),
        TargetDto(
          "SSH_INTERN_SERVER_DIRECT", "ssh://intern-host",
          setOf(
            Entry("user", "internuser"),
            Entry("privateKey", "/config/env/ssh/client-intern-id_edcsa.key")
          )
        )
      )
    )

    @BeforeAll
    fun setUp() {
      sshContainer =
        ComposeContainer(File("src/test/resources/blackbox/env/ssh/ssh-env-compose.yml"))
          .waitingFor("jump-host", Wait.forLogMessage(".*Server listening on.*", 1))
          .waitingFor("intern-host", Wait.forLogMessage(".*Server listening on.*", 1))
      sshContainer!!.start()

      // Post environment for SSH tests
      chutneyServerInfo?.let {
        HttpClient.post<Any>(it, "/api/v2/environments", om.writeValueAsString(sshEnv))
      }
    }

    @AfterAll
    fun cleanUp() {
      sshContainer?.stop()
      // Delete environment for SSH tests
      chutneyServerInfo?.let {
        HttpClient.delete<Any>(it, "/api/v2/environments/${sshEnv.name}", "")
      }
    }

    @Test
    fun `SSH Task test`() {
      softlyAssertLauncherRun(
        `SSH - Execute commands on server`() +
            `SSH - Execute shell on server`() +
            `SSH - Server is unreachable`()
      )
    }
  }

  @Test
  fun `Create scenario-campaign with specific ids`() {
    softlyAssertLauncherRun(
      listOf(
        createUpdateScenarioWithSpecificId(1234),
        createUpdateCampaignWithSpecificId(1234)
      )
    )
  }

  @Test
  fun `Agent test`() {
    Launcher().run(`We receive a network configuration to persist`(), environment)
  }

  private fun softlyAssertLauncherRun(scenarios: List<ChutneyScenario>) {
    SoftAssertions.assertSoftly { softly ->
      scenarios.forEach {
        softly.assertThatCode {
          Launcher().run(it, environment)
        }.`as` { it.title }.doesNotThrowAnyException()
      }
    }
  }
}

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
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.wait.strategy.Wait
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile
import java.io.File
import java.time.Duration

@Testcontainers
class AcceptanceTests {

  companion object {
    private const val ENVIRONMENT_NAME = "DEFAULT"
    private val om = jacksonObjectMapper()
      .registerModule(JavaTimeModule())
      .registerModule(ParanamerModule())

    private var chutneyServer: GenericContainer<Nothing>? = null
    private var testContainer: ComposeContainer? = null
    private var environment: ChutneyEnvironment = ChutneyEnvironment(ENVIRONMENT_NAME)

    private var actionHttpPort: Int? = null
    private var actionAmqpPort: Int? = null
    private var actionJakartaPort: Int? = null
    private var actionSshPort: Int? = null

    @JvmStatic
    @BeforeAll
    fun setUp() {
      // Start system under test : Chutney server + test env //
      val chutneyEnvTargets = startChutneyTestEnvironment()
      val chutneyTestEnvironment = EnvironmentDto("ACCEPTANCE", "The acceptance tests environment", chutneyEnvTargets)

      actionHttpPort = SocketUtils.freePortFromSystem()
      actionAmqpPort = SocketUtils.freePortFromSystem()
      System.setProperty("qpid.amqp_port", actionAmqpPort.toString())
      actionJakartaPort = SocketUtils.freePortFromSystem()
      actionSshPort = SocketUtils.freePortFromSystem()
      org.testcontainers.Testcontainers.exposeHostPorts(
        actionHttpPort!!, actionAmqpPort!!, actionJakartaPort!!, actionSshPort!!
      )

      // Setup system under test : Chutney server //
      val chutneyServerHost = testContainer!!.getServiceHost("chutney-server", 8443)
      val chutneyServerPort = testContainer!!.getServicePort("chutney-server", 8443)
      ChutneyServerInfo(
        "https://$chutneyServerHost:$chutneyServerPort", "admin", "admin",
        null, null, null
      ).let {
        // Authorizations
        val roles = AcceptanceTests::class.java.getResource("/blackbox/roles.json")!!.path
        HttpClient.post<Any>(it, "/api/v1/authorizations", File(roles).readText())
        // Environment
        HttpClient.post<Any>(it, "/api/v2/environments", om.writeValueAsString(chutneyTestEnvironment))
      }

      // Build launcher test environment //
      environment = ChutneyEnvironment(
        name = ENVIRONMENT_NAME, targets =
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

    private fun startChutneyTestEnvironment(): List<TargetDto> {
      //@formatter:off
      testContainer =
        ComposeContainer(File("src/test/resources/blackbox/ssh-jumphost-compose.yml"))
          .withExposedService("chutney-server", 8443,
            Wait.forListeningPort().forPorts(8443).withStartupTimeout(Duration.ofSeconds(80))
          )
          .withEnv("CHUTNEY_CONFIG", MountableFile.forClasspathResource("/blackbox/").resolvedPath)
      testContainer!!.start()

      val jumpServerUrl = "ssh://jump-host"
      return listOf(
        TargetDto("SSH_JUMP_SERVER", jumpServerUrl,
          setOf(
            Entry("user", "jumpuser"),
            Entry("privateKey", "/config/env/ssh/client-jump-id_ecdsa")
          )
        ),
        TargetDto("SSH_INTERN_SERVER", "ssh://intern-host",
          setOf(
            Entry("user", "internuser"),
            Entry("privateKey", "/config/env/ssh/client-intern-id_edcsa"),
            Entry("proxy", jumpServerUrl),
            Entry("proxyUser", "jumpuser"),
            Entry("proxyPrivateKey", "/config/env/ssh/client-jump-id_ecdsa")
          )
        ),
        TargetDto("SSH_INTERN_SERVER_DIRECT", "ssh://intern-host",
          setOf(
            Entry("user", "internuser"),
            Entry("privateKey", "/config/env/ssh/client-intern-id_edcsa")
          )
        )
      )
      //@formatter:on
    }

    @JvmStatic
    @AfterAll
    fun cleanUp() {
      chutneyServer?.stop()
      testContainer?.stop()
    }
  }

  @Test
  fun `Execution by campaign id with 2 scenarios`() {
    listOf(
      executeCampaignById,
      executeCampaignByName,
      executeForSurefireReport,
      unknownCampaignById,
      unknownCampaignByName
    ).forEach {
      Launcher().run(it, environment)
    }
  }

  @Test
  fun `Support testcase edition metadata`() {
    listOf(
      readTestCaseMetadataScenario,
      readTestCaseAfterUpdateScenario,
      updateTestCaseWithBadVersionScenario
    ).forEach {
      Launcher().run(it, environment)
    }
  }

  @Test
  fun `Support testcase editions`() {
    listOf(
      `Request testcase edition`,
      `Request for a second time testcase edition`,
      `End testcase edition`,
      `Edition time to live`
    ).forEach {
      Launcher().run(it, environment)
    }
  }

  @Test
  fun `SQL Task test`() {
    listOf(
      `Sql query success`,
      `Sql query wrong table`
    ).forEach {
      Launcher().run(it, environment)
    }
  }

  @Test
  fun `Success feature`() {
    listOf(
      `Direct Success`,
      `Substeps Success`
    ).forEach {
      Launcher().run(it, environment)
    }
  }

  @Test
  fun `Amqp feature`() {
    Launcher().run(`amqp test all steps`(actionAmqpPort!!), environment)
  }

  @Test
  fun `Kafka all Tasks test`() {
    listOf(
      `Kafka basic publish wrong url failure`,
      `Kafka basic publish success`
    ).forEach {
      Launcher().run(it, environment)
    }
  }

  @Test
  fun `Roles declarations and users associations`() {
    listOf(
      `Declare a new role with its authorizations`,
      `Add and remove user to-from an existing role`
    ).forEach {
      Launcher().run(it, environment)
    }
  }

  @Test
  fun `Execution success action`() {
    listOf(
      `Action instantiation and execution of a success scenario`,
      `Task instantiation and execution of a failed scenario`,
      `Task instantiation and execution of a sleep scenario`,
      `Task instantiation and execution of a debug scenario`
    ).forEach {
      Launcher().run(it, environment)
    }
  }

  @Test
  fun `Finally actions`() {
    Launcher().run(`Step of a type self registering as Finally Action does not create an infinite loop`, environment)
  }

  @Test
  fun `Execution with jsonPath function`() {
    listOf(
      `Scenario execution with simple json value extraction`,
      `Scenario execution with multiple json value extraction`,
      `Scenario execution with json object value extraction`
    ).forEach {
      Launcher().run(it, environment)
    }
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
    listOf(
      `Micrometer counter meter`,
      `Micrometer timer meter`,
      `Micrometer timer meter with start and stop`,
      `Micrometer gauge meter`,
      `Micrometer distribution summary meter`
    ).forEach {
      Launcher().run(it, environment)
    }
  }

  @Test
  fun `Assertions Task test`() {
    listOf(
      `Execution by UI controller`,
      `All in one assertions`,
      `Test xsd actions`
    ).forEach {
      Launcher().run(it, environment)
    }
  }

  @Test
  fun `Final action for registering final actions for a testcase`() {
    listOf(
      `Register simple success action`,
      `Register multiple actions with one complex, ie with inputs and strategy`,
      `Register final action with validations on outputs`
    ).forEach {
      Launcher().run(it, environment)
    }
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

  @Test
  fun `SSH Task test`() {
    softlyAssertLauncherRun(
      `SSH - Execute commands on server`() +
          `SSH - Execute shell on server`() +
          `SSH - Server is unreachable`() +
          `SSH - Start server and Execute some commands`(actionSshPort!!)
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

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance

import com.chutneytesting.acceptance.tests.*
import com.chutneytesting.acceptance.tests.actions.*
import com.chutneytesting.acceptance.tests.`We receive a network configuration to persist`
import com.chutneytesting.acceptance.tests.edition.*
import com.chutneytesting.acceptance.tests.engine.*
import com.chutneytesting.acceptance.tests.`Add and remove user to-from an existing role`
import com.chutneytesting.acceptance.tests.`Declare a new role with its authorizations`
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.ChutneyTarget
import com.chutneytesting.kotlin.launcher.Launcher
import com.chutneytesting.kotlin.util.ChutneyServerInfo
import com.chutneytesting.kotlin.util.HttpClient
import com.chutneytesting.tools.SocketUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.MountableFile
import java.io.File
import java.nio.file.Files
import java.time.Duration

@Testcontainers
class AcceptanceTests {

  companion object {
    private const val ENVIRONMENT_NAME = "DEFAULT"

    private var adminServerInfo: ChutneyServerInfo? = null
    private var chutneyServer: GenericContainer<Nothing>? = null
    private var environment: ChutneyEnvironment = ChutneyEnvironment(ENVIRONMENT_NAME)

    private var actionHttpPort: Int? = null
    private var actionAmqpPort: Int? = null
    private var actionJakartaPort: Int? = null

    @JvmStatic
    @BeforeAll
    fun setUp() {
      val tempDirectory = Files.createTempDirectory("chutney-acceptance-test-")
      val memAuthConfigFile = File(AcceptanceTests::class.java.getResource("/blackbox/application-mem-auth.yml")!!.path)

      // Copy mem-auth config
      Files.copy(memAuthConfigFile.toPath(), tempDirectory.resolve("application-mem-auth.yml"))

      // Start server
      chutneyServer = GenericContainer<Nothing>("ghcr.io/chutney-testing/chutney/chutney-server:latest").apply {
        withStartupTimeout(Duration.ofSeconds(80))
        withExposedPorts(
            8443, // Chutney
        )
        withNetwork(network)
        withCopyFileToContainer(
            MountableFile.forClasspathResource("/blackbox/"),
            "/config"
        )
      }
      actionHttpPort = SocketUtils.freePortFromSystem()
      actionAmqpPort = 5672
      actionJakartaPort = SocketUtils.freePortFromSystem()
      org.testcontainers.Testcontainers.exposeHostPorts(
        actionHttpPort!!,
        actionAmqpPort!!,
        actionJakartaPort!!
      )

      chutneyServer!!.start()
      adminServerInfo = ChutneyServerInfo("https://${chutneyServer?.host}:${chutneyServer?.firstMappedPort}", "admin", "admin", null, null, null)

      // Set authorizations
      val roles = AcceptanceTests::class.java.getResource("/blackbox/roles.json")!!.path
      HttpClient.post<Any>(adminServerInfo!!, "/api/v1/authorizations", File(roles).readText())

      environment = ChutneyEnvironment(name = ENVIRONMENT_NAME, targets =
      listOf(
          ChutneyTarget("CHUTNEY_LOCAL", "https://${chutneyServer?.host}:${chutneyServer?.getMappedPort(8443)}", mapOf("username" to "admin", "password" to "admin")),
          ChutneyTarget("CHUTNEY_LOCAL_NO_USER", "https://${chutneyServer?.host}:${chutneyServer?.getMappedPort(8443)}", emptyMap()))
      )
    }

    @JvmStatic
    @AfterAll
    fun cleanUp() {
      chutneyServer?.stop()
    }
  }

  @Test
  fun `Execution by campaign id with 2 scenarios`() {
    Launcher().run(executeCampaignById, environment)
    Launcher().run(executeCampaignByName, environment)
    Launcher().run(executeForSurefireReport, environment)
    Launcher().run(unknownCampaignById, environment)
    Launcher().run(unknownCampaignByName, environment)
  }

  @Test
  fun `Support testcase edition metadata`() {
    Launcher().run(readTestCaseMetadataScenario, environment)
    Launcher().run(readTestCaseAfterUpdateScenario, environment)
    Launcher().run(updateTestCaseWithBadVersionScenario, environment)
  }

  @Test
  fun `Support testcase editions`() {
    Launcher().run(`Request testcase edition`, environment)
    Launcher().run(`Request for a second time testcase edition`, environment)
    Launcher().run(`End testcase edition`, environment)
    Launcher().run(`Edition time to live`, environment)
  }

  @Test
  fun `SQL Task test`() {
    Launcher().run(`Sql query success`, environment)
    Launcher().run(`Sql query wrong table`, environment)
  }

  @Test
  fun `Success feature`() {
    Launcher().run(`Direct Success`, environment)
    Launcher().run(`Substeps Success`, environment)
  }

  @Test
  fun `Amqp feature`() {
    Launcher().run(`amqp test all steps`, environment)
  }

  @Test
  fun `Kafka all Tasks test`() {
    Launcher().run(`Kafka basic publish wrong url failure`, environment)
    Launcher().run(`Kafka basic publish success`, environment)
  }

  @Test
  fun `Roles declarations and users associations`() {
    Launcher().run(`Declare a new role with its authorizations`, environment)
    Launcher().run(`Add and remove user to-from an existing role`, environment)
  }

  @Test
  fun `Execution success action`() {
    Launcher().run(`Action instantiation and execution of a success scenario`, environment)
    Launcher().run(`Task instantiation and execution of a failed scenario`, environment)
    Launcher().run(`Task instantiation and execution of a sleep scenario`, environment)
    Launcher().run(`Task instantiation and execution of a debug scenario`, environment)
  }

  @Test
  fun `Finally actions`() {
    Launcher().run(`Step of a type self registering as Finally Action does not create an infinite loop`, environment)
  }

  @Test
  fun `Execution with jsonPath function`() {
    Launcher().run(`Scenario execution with simple json value extraction`, environment)
    Launcher().run(`Scenario execution with multiple json value extraction`, environment)
    Launcher().run(`Scenario execution with json object value extraction`, environment)
  }

  @Test
  fun `Execution retry strategy`() {
    Launcher().run( `Retry should stop after success assertion`, environment)
  }

  @Test
  fun `Replace scenario parameters with data set or global var values`() {
    Launcher().run( `Execute gwt scenario with global vars`, environment)
  }

  @Test
  fun `Engine actions exposition`() {
    listOf(
      Pair("debug", "[[{\"name\": \"filters\",\"type\": \"java.util.List\"}]]"),
      Pair("assert", "[[{\"name\": \"asserts\",\"type\": \"java.util.List\"}]]"),
      Pair("compare", "[[{\"name\": \"actual\",\"type\": \"java.lang.String\"},{\"name\": \"expected\",\"type\": \"java.lang.String\"},{\"name\": \"mode\",\"type\": \"java.lang.String\"}]]")
    ).forEach {
      Launcher().run( `Retrieve action action-id from all actions and by identifier`(it.first, it.second), environment)
    }

  }
  @Test
  fun `HTTP server Task test`() {
    Launcher().run( `Http post request local server endpoint`, environment)
  }

  @Test
  fun `HTTP client Task test`() {
    listOf(
      Pair("GET", "uri: /notused"),
      Pair("DELETE", "uri: /notused"),
      Pair("POST", "uri: /notused  body: cool buddy"),
      Pair("PUT", "uri: /notused body: cool buddy"),
    ).forEach {
      Launcher().run( `Http (verb) request wrong url`(it.first, it.second), environment)
    }

    listOf(
      Pair("GET", "uri: /mock/get"),
      Pair("DELETE", "uri: /mock/delete"),
      Pair("POST", "uri: /mock/post  body: cool buddy"),
      Pair("PUT", "uri: /mock/put body: cool buddy"),
    ).forEach {
      Launcher().run( `Http (verb) request local valid endpoint`(it.first, it.second, actionHttpPort!!), environment)
    }
  }

  @Test
  fun `Micrometer Tasks test`() {
    Launcher().run( `Micrometer counter meter`, environment)
    Launcher().run( `Micrometer timer meter`, environment)
    Launcher().run( `Micrometer timer meter with start and stop`, environment)
    Launcher().run( `Micrometer gauge meter`, environment)
    Launcher().run( `Micrometer distribution summary meter`, environment)
  }

  @Test
  fun `Assertions Task test`() {
    Launcher().run( `Execution by UI controller`, environment)
    Launcher().run( `All in one assertions`, environment)
    Launcher().run( `Test xsd actions`, environment)
  }

  @Test
  fun `Final action for registering final actions for a testcase`() {
    Launcher().run( `Register simple success action`, environment)
    Launcher().run( `Register multiple actions with one complex, ie with inputs and strategy`, environment)
    Launcher().run( `Register final action with validations on outputs`, environment)
  }

  @Test
  fun `Jms Task test`() {
    Launcher().run( `Jms sender then clean then send and listen it on embedded broker`(), environment)
    listOf(
      Pair("sender", "destination: test \\n body: something"),
      Pair("clean-queue", "destination: test \\n bodySelector: selector"),
      Pair("listener", "destination: test \\n bodySelector: selector")
    ).forEach {
      Launcher().run( `Jms jmsAction wrong url`(it.first, it.second), environment)
    }
  }

  @Test
  fun `Jakarta Task test`() {
    Launcher().run( `Jakarta sender then clean then send and listen it on embedded broker`(actionJakartaPort!!), environment)
    listOf(
      Pair("sender", "destination: test \\n body: something"),
      Pair("clean", "destination: test \\n bodySelector: selector"),
      Pair("listener", "destination: test \\n bodySelector: selector")
    ).forEach {
      Launcher().run( `Jakarta actionInputs wrong url`(it.first, it.second), environment)
    }
  }

  @Test
  fun `SSH Task test`() {
    Launcher().run( `Scenario execution unable to login, status SUCCESS and command stderr`(), environment)
    Launcher().run(`Scenario execution with multiple ssh action`(), environment)
  }

  @Test
  fun `Agent test`() {
    Launcher().run( `We receive a network configuration to persist`(), environment)
  }
}

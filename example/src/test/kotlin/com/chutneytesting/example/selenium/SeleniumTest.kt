/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.example.selenium

import com.chutneytesting.example.scenario.selenium_scenario
import com.chutneytesting.kotlin.dsl.Environment
import com.chutneytesting.kotlin.launcher.Launcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.openqa.selenium.firefox.FirefoxOptions
import org.testcontainers.containers.BrowserWebDriverContainer
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.Network
import org.testcontainers.images.PullPolicy
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Duration

@Testcontainers
class SeleniumTest {

    private val network = Network.newNetwork()
    private val chutneyServer =
        GenericContainer(DockerImageName.parse("ghcr.io/chutney-testing/chutney/chutney-server:latest"))
        .withImagePullPolicy(PullPolicy.alwaysPull())
        .withNetworkAliases("chutneyServer")
        .withExposedPorts(8443)
        .withNetwork(network)
        .withStartupTimeout(Duration.ofSeconds(80))

    private val webDriverContainer = BrowserWebDriverContainer()
        .withCapabilities(FirefoxOptions().setAcceptInsecureCerts(true))
        .withNetwork(network)


    @BeforeEach
    fun setUp() {
        chutneyServer.start()
        webDriverContainer.start()
    }

    @AfterEach
    fun tearDown() {
        webDriverContainer.stop()
        chutneyServer.stop()
    }

    @Test
    fun `Selenium test`() {
        val env = Environment("Global", "") {}
        Launcher().run(selenium_scenario(webDriverContainer.seleniumAddress.toString()), env)
    }

}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.execution

import com.chutneytesting.engine.api.execution.StatusDto
import com.chutneytesting.environment.domain.exception.EnvironmentNotFoundException
import com.chutneytesting.environment.domain.exception.UnresolvedEnvironmentException
import com.chutneytesting.kotlin.asResource
import com.chutneytesting.kotlin.dsl.ForStrategy
import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.SuccessAction
import com.chutneytesting.kotlin.execution.report.AnsiReportWriter
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.SoftAssertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir
import java.io.File
import java.nio.file.Path

class ExecutionServiceTest {

    @Test
    fun `should retrieve empty environment when no json env defined and none asked for`(@TempDir tempDir: Path) {
        val sut = ExecutionService(tempDir.toAbsolutePath().toString())

        val noneEnvCall = sut.getEnvironment()
        assertThat(noneEnvCall.targets).isEmpty()

        val nullEnvCall = sut.getEnvironment(null)
        assertThat(nullEnvCall.targets).isEmpty()
    }

    @Test
    fun `should retrieve the environment when only one env defined and none asked for`() {
        val sut = ExecutionService(File("execution/oneEnv".asResource().path).path)

        val noneEnvCall = sut.getEnvironment()
        assertThat(noneEnvCall.name).isEqualTo("ALONE")

        val nullEnvCall = sut.getEnvironment(null)
        assertThat(nullEnvCall.name).isEqualTo("ALONE")
    }

    @Test
    fun `should throw exception when multi env defined and none asked for`() {
        val sut = ExecutionService(File("execution/multiEnv".asResource().path).path)

        assertThrows<UnresolvedEnvironmentException> {
            sut.getEnvironment()
        }

        assertThrows<UnresolvedEnvironmentException> {
            sut.getEnvironment(null)
        }
    }

    @Test
    fun `should throw exception when env not found`() {
        val sut = ExecutionService(File("execution/multiEnv".asResource().path).path)

        assertThrows<EnvironmentNotFoundException> {
            sut.getEnvironment("UNDEFINED")
        }
    }

    @Test
    fun `should retrieve an environment when env defined`() {
        val sut = ExecutionService(File("execution/oneEnv".asResource().path).path)

        val environment = sut.getEnvironment("ALONE")

        assertThat(environment.name).isEqualTo("ALONE")
        assertThat(environment.description).isEqualTo("alone environment for test")

        assertThat(environment.targets).hasSize(1)
        val target = environment.targets[0]
        val targetPropertiesAssert = SoftAssertions()
        targetPropertiesAssert.assertThat(target.name).isEqualTo("target")
        targetPropertiesAssert.assertThat(target.url).isEqualTo("url")
        targetPropertiesAssert.assertThat(target.properties)
            .containsEntry("key", "value")
            .containsEntry("username", "username")
            .containsEntry("password", "password")
            .containsEntry("trustStore", "path")
            .containsEntry("trustStorePassword", "password")
            .containsEntry("keyStore", "path")
            .containsEntry("keyStorePassword", "password")
            .containsEntry("keyPassword", "password")
            .containsEntry("privateKey", "path")
        targetPropertiesAssert.assertAll()
    }

    @Test
    fun `should execute with dataset`() {
        val dataset = listOf(
            mapOf(
                "key1" to "X",
                "key2" to "Y"
            ),
            mapOf(
                "key1" to "A",
                "key2" to "B"
            )
        )
        val scenario = Scenario(title = "scenario with for") {
            When("<i> step description - \${#key1} - \${#key2}", strategy = ForStrategy()) {
                SuccessAction()
            }
        }
        val sut = ExecutionService()
        val report = sut.waitLastReport(sut.execute(scenario, dataset = dataset))

        assertThat(report.status).isEqualTo(StatusDto.SUCCESS)
        assertThat(report.steps[0].steps).hasSize(2)
        assertThat(report.steps[0].steps[0].name).isEqualTo("0 step description - X - Y") // First Iteration
        assertThat(report.steps[0].steps[1].name).isEqualTo("1 step description - A - B") // Second Iteration
    }

    @Test
    fun `should iterate once over dataset constants`() {
        val constants = mapOf(
            "key1" to "X",
            "key2" to "Y"
        )
        val scenario = Scenario(title = "scenario with for") {
            When("<i> step description - \${#key1} - \${#key2}", strategy = ForStrategy()) {
                SuccessAction(
                )
            }
        }
        val sut = ExecutionService()
        val report = sut.waitLastReport(sut.execute(scenario, constants = constants))

        assertThat(report.status).isEqualTo(StatusDto.SUCCESS)
        assertThat(report.steps[0].steps).hasSize(1)
        assertThat(report.steps[0].steps[0].name).isEqualTo("0 step description - X - Y") // First Iteration
    }

}

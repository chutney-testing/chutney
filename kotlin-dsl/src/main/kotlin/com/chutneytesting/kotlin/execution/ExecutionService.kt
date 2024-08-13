/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.execution

import com.chutneytesting.ExecutionConfiguration
import com.chutneytesting.engine.api.execution.DatasetDto
import com.chutneytesting.engine.api.execution.ExecutionRequestDto
import com.chutneytesting.engine.api.execution.StepExecutionReportDto
import com.chutneytesting.environment.EnvironmentConfiguration
import com.chutneytesting.environment.api.environment.dto.EnvironmentDto
import com.chutneytesting.kotlin.dsl.ChutneyEnvironment
import com.chutneytesting.kotlin.dsl.ChutneyScenario
import com.chutneytesting.kotlin.dsl.ChutneyTarget

const val CHUTNEY_ROOT_PATH_DEFAULT = ".chutney"
const val CHUTNEY_ENV_ROOT_PATH_DEFAULT = "$CHUTNEY_ROOT_PATH_DEFAULT/environments"


class ExecutionService(
    environmentJsonRootPath: String = CHUTNEY_ENV_ROOT_PATH_DEFAULT
) {

    private val executionConfiguration = ExecutionConfiguration()
    private val embeddedEnvironmentApi = EnvironmentConfiguration(environmentJsonRootPath).embeddedEnvironmentApi


    fun execute(
        scenario: ChutneyScenario,
        environment: ChutneyEnvironment,
        constants: Map<String, String> = emptyMap(),
        dataset: List<Map<String,String>> = emptyList()
    ): Long {
        val datasetDto = DatasetDto(constants, dataset);
        return executionConfiguration.embeddedTestEngine()
            .executeAsync(
                ExecutionRequestDto(
                    ExecutionRequestMapper.mapScenarioToExecutionRequest(scenario, environment),
                    com.chutneytesting.engine.api.execution.EnvironmentDto(environment.name, environment.variables),
                    datasetDto
                )
            )
    }

    fun execute(
        scenario: ChutneyScenario,
        environmentName: String? = null,
        constants: Map<String, String> = emptyMap(),
        dataset: List<Map<String,String>> = emptyList()
    ): Long {
        return execute(scenario, getEnvironment(environmentName), constants, dataset)
    }

    fun waitLastReport(executionId: Long): StepExecutionReportDto {
        return executionConfiguration.embeddedTestEngine()
            .receiveNotification(executionId)
            .blockingLast()
    }

    fun getEnvironment(environmentName: String? = null): ChutneyEnvironment {
        val executionEnv = environmentName.takeUnless { it.isNullOrBlank() } ?: embeddedEnvironmentApi.defaultEnvironmentName()
        val environmentDto = embeddedEnvironmentApi.getEnvironment(executionEnv)
        return mapEnvironmentNameToChutneyEnvironment(environmentDto)
    }

    private fun mapEnvironmentNameToChutneyEnvironment(environmentDto: EnvironmentDto): ChutneyEnvironment {
        return ChutneyEnvironment(
            name = environmentDto.name,
            description = environmentDto.description,
            targets = environmentDto.targets.map { targetDto ->
                ChutneyTarget(
                    name = targetDto.name,
                    url = targetDto.url,
                    properties = targetDto.propertiesToMap()
                )
            }
        )
    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.environment;

import static org.assertj.core.api.Assertions.assertThat;

import com.chutneytesting.environment.api.environment.dto.EnvironmentDto;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class EnvironmentConfigurationTest {

    @Test
    void should_give_access_to_api_when_instantiated(@TempDir Path tempPath) {
        // When
        EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(tempPath.toString());
        // Then
        assertThat(environmentConfiguration.getEmbeddedEnvironmentApi()).isNotNull();
        assertThat(environmentConfiguration.getEmbeddedTargetApi()).isNotNull();
        assertThat(environmentConfiguration.getEmbeddedVariableApi()).isNotNull();
    }

    @Test
    void should_create_default_environment_at_started_when_environment_list_is_empty(@TempDir Path tempPath) {
        //Given
        EnvironmentDto expected = new EnvironmentDto("DEFAULT");

        // When
        EnvironmentConfiguration environmentConfiguration = new EnvironmentConfiguration(tempPath.toString());

        // Then
        assertThat(environmentConfiguration.getEmbeddedEnvironmentApi().listEnvironments().size()).isEqualTo(1);
        assertThat(environmentConfiguration.getEmbeddedEnvironmentApi().getEnvironment("DEFAULT")).isEqualTo(expected);
    }
}

/*
 * Copyright 2017-2023 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chutneytesting.environment;

import com.chutneytesting.environment.api.environment.EmbeddedEnvironmentApi;
import com.chutneytesting.environment.api.target.EmbeddedTargetApi;
import com.chutneytesting.environment.api.variable.EnvironmentVariableApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvironmentSpringConfiguration {

    public static final String ENVIRONMENT_CONFIGURATION_FOLDER = "${chutney.environment.configuration-folder:~/.chutney/conf/environment}";


    @Bean
    EnvironmentConfiguration environmentConfiguration(@Value(ENVIRONMENT_CONFIGURATION_FOLDER) String storeFolderPath) {
        return new EnvironmentConfiguration(storeFolderPath);
    }
    @Bean
    EmbeddedEnvironmentApi environmentEmbeddedApplication(EnvironmentConfiguration environmentConfiguration) {
        return environmentConfiguration.getEmbeddedEnvironmentApi();
    }

    @Bean
    EmbeddedTargetApi targetEmbeddedApplication(EnvironmentConfiguration environmentConfiguration) {
        return environmentConfiguration.getEmbeddedTargetApi();
    }

    @Bean
    EnvironmentVariableApi variableEmbeddedApplication(EnvironmentConfiguration environmentConfiguration) {
        return environmentConfiguration.getEmbeddedVariableApi();
    }
}

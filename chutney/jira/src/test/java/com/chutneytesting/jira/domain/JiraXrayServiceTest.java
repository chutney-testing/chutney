/*
 *  Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.chutneytesting.jira.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chutneytesting.jira.domain.exception.NoJiraConfigurationException;
import java.lang.reflect.Field;
import org.junit.jupiter.api.Test;

class JiraXrayServiceTest {

    @Test
    void update_jira_configuration_before_each_api_calls() throws Exception {
        // Given
        var jiraRepository = mock(JiraRepository.class);
        var initialJiraConfiguration = new JiraServerConfiguration("", "", "", null, null, null);
        when(jiraRepository.loadServerConfiguration()).thenReturn(initialJiraConfiguration);

        var jiraXrayClientFactory = mock(JiraXrayClientFactory.class);
        var jiraXrayApi = mock(JiraXrayApi.class);
        when(jiraXrayClientFactory.create(any())).thenReturn(jiraXrayApi);

        JiraXrayService sut = new JiraXrayService(jiraRepository, jiraXrayClientFactory);
        Field jiraConfigurationField = sut.getClass().getDeclaredField("jiraServerConfiguration");
        jiraConfigurationField.setAccessible(true);

        // When empty initial configuration
        assertThatThrownBy(() -> sut.getTestExecutionScenarios("NOP-666"))
            .isInstanceOf(NoJiraConfigurationException.class);

        assertThat(jiraConfigurationField.get(sut)).isEqualTo(initialJiraConfiguration);

        // When new configuration
        var newJiraConfiguration = new JiraServerConfiguration("http://jira.server", "", "", null, null, null);
        when(jiraRepository.loadServerConfiguration()).thenReturn(newJiraConfiguration);
        sut.getTestExecutionScenarios("NOP-666");

        assertThat(jiraConfigurationField.get(sut)).isEqualTo(newJiraConfiguration);
        verify(jiraXrayApi).getTestExecutionScenarios("NOP-666");
    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.jira.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableJiraTestExecutionDto.class)
@JsonDeserialize(as = ImmutableJiraTestExecutionDto.class)
@Value.Style(jdkOnly = true)
public interface JiraTestExecutionDto {

    String id();

    List<JiraDto> jiraScenarios();

}

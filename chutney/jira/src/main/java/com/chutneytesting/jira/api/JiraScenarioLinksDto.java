/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.jira.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Map;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableJiraScenarioLinksDto.class)
@JsonDeserialize(as = ImmutableJiraScenarioLinksDto.class)
@Value.Style(jdkOnly = true)
public interface JiraScenarioLinksDto {

    String id();

    String chutneyId();

    Map<String, String> datasetLinks();
}

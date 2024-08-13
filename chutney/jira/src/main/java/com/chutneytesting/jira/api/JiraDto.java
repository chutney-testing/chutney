/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.jira.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableJiraDto.class)
@JsonDeserialize(as = ImmutableJiraDto.class)
@Value.Style(jdkOnly = true)
public interface JiraDto {

    String id();

    String chutneyId();

    Optional<String> executionStatus();
}

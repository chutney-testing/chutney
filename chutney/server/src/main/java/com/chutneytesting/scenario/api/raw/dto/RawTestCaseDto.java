/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.scenario.api.raw.dto;

import static java.time.Instant.now;

import com.chutneytesting.server.core.domain.security.User;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableRawTestCaseDto.class)
@JsonDeserialize(as = ImmutableRawTestCaseDto.class)
@Value.Style(jdkOnly = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public interface RawTestCaseDto {

    @JsonProperty("content")
    String scenario();

    Optional<String> id();

    String title();

    Optional<String> description();

    List<String> tags();

    Optional<String> defaultDataset();

    @Value.Default()
    default Instant creationDate() {
        return now();
    }

    @Value.Default()
    default String author() {
        return User.ANONYMOUS.id;
    }

    @Value.Default()
    default Instant updateDate() {
        return now();
    }

    @Value.Default()
    default Integer version() {
        return 1;
    }
}

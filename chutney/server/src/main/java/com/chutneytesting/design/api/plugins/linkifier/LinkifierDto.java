/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.design.api.plugins.linkifier;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

@Value.Immutable
@JsonSerialize(as = ImmutableLinkifierDto.class)
@JsonDeserialize(as = ImmutableLinkifierDto.class)
@Value.Style(jdkOnly = true)
public interface LinkifierDto {

    String pattern();

    String link();

    String id();

}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.api.execution;

import java.util.Map;

public record EnvironmentDto(String name, Map<String,String> variables) {
}

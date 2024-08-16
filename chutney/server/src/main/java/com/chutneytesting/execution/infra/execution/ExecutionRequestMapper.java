/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.infra.execution;

import com.chutneytesting.engine.api.execution.ExecutionRequestDto;
import com.chutneytesting.server.core.domain.execution.ExecutionRequest;

public interface ExecutionRequestMapper {

    ExecutionRequestDto toDto(ExecutionRequest executionRequest);

}

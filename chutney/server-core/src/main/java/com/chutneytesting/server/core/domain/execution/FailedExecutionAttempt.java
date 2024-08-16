/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.execution;

public class FailedExecutionAttempt extends RuntimeException {
    public final Long executionId;
    public final String title;

    public FailedExecutionAttempt(Exception exception, Long executionId, String title) {
        super(exception);
        this.executionId = executionId;
        this.title = title;
    }
}

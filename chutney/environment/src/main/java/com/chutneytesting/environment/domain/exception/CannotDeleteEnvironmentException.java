/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.environment.domain.exception;

@SuppressWarnings("serial")
public class CannotDeleteEnvironmentException extends RuntimeException {

    public CannotDeleteEnvironmentException(String message) {
        super(message);
    }

    public CannotDeleteEnvironmentException(String message, Exception cause) {
        super(message, cause);
    }
}

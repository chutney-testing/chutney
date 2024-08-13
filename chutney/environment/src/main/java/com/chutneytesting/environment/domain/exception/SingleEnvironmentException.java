/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.environment.domain.exception;

public class SingleEnvironmentException extends RuntimeException {

    public SingleEnvironmentException(String message) {
            super(message);
        }

    public SingleEnvironmentException(String message, Exception cause) {
            super(message, cause);
    }
}

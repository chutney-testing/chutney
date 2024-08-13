/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.environment.domain.exception;

@SuppressWarnings("serial")
public class EnvVariableNotFoundException extends RuntimeException {
    public EnvVariableNotFoundException(String message) {
        super(message);
    }
}

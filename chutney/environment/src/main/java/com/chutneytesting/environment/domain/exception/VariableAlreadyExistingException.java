/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.environment.domain.exception;

@SuppressWarnings("serial")
public class VariableAlreadyExistingException extends RuntimeException {

    public VariableAlreadyExistingException(String message) {
        super(message);
    }

}

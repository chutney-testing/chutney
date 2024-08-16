/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.delegation;

@SuppressWarnings("serial")
public class CannotDelegateException extends RuntimeException {

    public CannotDelegateException(String message) {
        super(message);
    }

}

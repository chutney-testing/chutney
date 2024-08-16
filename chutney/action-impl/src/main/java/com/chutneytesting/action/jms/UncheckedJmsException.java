/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.jms;

@SuppressWarnings("serial")
class UncheckedJmsException extends RuntimeException {

    public UncheckedJmsException(String message, Exception cause) {
        super(message, cause);
    }
}

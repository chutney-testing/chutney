/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.spi.injectable;

public interface Logger {

    void info(String message);

    void error(String message);

    void error(Throwable exception);

    Logger reportOnly();
}

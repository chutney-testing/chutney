/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.function;

import com.chutneytesting.action.spi.SpelFunction;

public class GenerateFunction {

    @SpelFunction
    public static Generate generate() {
        return new Generate();
    }
}

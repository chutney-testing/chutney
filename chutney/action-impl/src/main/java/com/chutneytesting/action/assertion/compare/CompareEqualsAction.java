/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.assertion.compare;

import com.chutneytesting.action.spi.ActionExecutionResult;
import com.chutneytesting.action.spi.injectable.Logger;
import java.util.Objects;

public class CompareEqualsAction implements CompareExecutor {

    @Override
    public ActionExecutionResult compare(Logger logger, String actual, String expected) {
        if (Objects.equals(actual, expected)) {
            logger.info("[" + expected + "] EQUALS [" + actual + "]");
            return ActionExecutionResult.ok();
        } else {
            logger.error("[" + expected + "] NOT EQUALS [" + actual + "]");
            return ActionExecutionResult.ko();
        }
    }
}

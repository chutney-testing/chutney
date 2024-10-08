/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.assertion.compare;

import com.chutneytesting.action.spi.ActionExecutionResult;
import com.chutneytesting.action.spi.injectable.Logger;

public class CompareNotContainsAction implements CompareExecutor {

    @Override
    public ActionExecutionResult compare(Logger logger, String actual, String expected) {
        if (actual.contains(expected)) {
            logger.error("[" + actual + "] CONTAINS [" + expected + "]");
            return ActionExecutionResult.ko();
        } else {
            logger.info("[" + actual + "] NOT CONTAINS [" + expected + "]");
            return ActionExecutionResult.ok();
        }
    }
}

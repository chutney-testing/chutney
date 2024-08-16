/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.context;

import com.chutneytesting.action.spi.Action;
import com.chutneytesting.action.spi.ActionExecutionResult;
import com.chutneytesting.action.spi.injectable.Logger;
import java.time.Instant;

public class FailAction implements Action {

    private final Logger logger;

    public FailAction(Logger logger) {
        this.logger = logger;
    }

    @Override
    public ActionExecutionResult execute() {
        logger.error("Failed at "+ Instant.now());
        return ActionExecutionResult.ko();
    }

}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.context;

import com.chutneytesting.action.spi.Action;
import com.chutneytesting.action.spi.ActionExecutionResult;

public class SuccessAction implements Action {

    public SuccessAction() {
    }

    @Override
    public ActionExecutionResult execute() {
        return ActionExecutionResult.ok();
    }
}

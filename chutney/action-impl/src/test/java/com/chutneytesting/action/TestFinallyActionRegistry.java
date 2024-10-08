/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action;

import com.chutneytesting.action.spi.FinallyAction;
import com.chutneytesting.action.spi.injectable.FinallyActionRegistry;
import java.util.ArrayList;
import java.util.List;

public class TestFinallyActionRegistry implements FinallyActionRegistry {

    public final List<FinallyAction> finallyActions = new ArrayList<>();

    @Override
    public void registerFinallyAction(FinallyAction finallyAction) {
        finallyActions.add(finallyAction);
    }
}

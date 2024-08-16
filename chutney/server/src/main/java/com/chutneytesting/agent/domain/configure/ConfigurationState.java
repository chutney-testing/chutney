/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.agent.domain.configure;

public enum ConfigurationState {
    NOT_STARTED, EXPLORING, WRAPING_UP, FINISHED;

    public boolean canChangeTo(ConfigurationState state) {
        return state.ordinal() == ordinal() + 1;
    }
}

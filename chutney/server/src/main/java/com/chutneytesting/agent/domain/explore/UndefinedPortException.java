/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.agent.domain.explore;

public class UndefinedPortException extends RuntimeException {

    public UndefinedPortException(String url, String protocol) {
        super("Port is not defined on [" + url + "]. Cannot default port for [" + protocol + "] protocol.");
    }

}

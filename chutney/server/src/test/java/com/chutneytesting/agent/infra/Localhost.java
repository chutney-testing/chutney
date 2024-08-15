/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.agent.infra;

import java.io.UncheckedIOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class Localhost {

    private static InetAddress localHost;

    static {
        try {
            localHost = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new UncheckedIOException(e);
        }
    }

    static String ip() {
        return localHost.getHostAddress();
    }
}

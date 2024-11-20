/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.ssh;

import static org.junit.platform.commons.util.StringUtils.isNotBlank;

import com.chutneytesting.action.spi.injectable.Target;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

public class Connection {

    private static final String EMPTY = "";

    public final String serverHost;
    public final int serverPort;
    public final String username;
    public final String password;
    public final String privateKey;
    public final String passphrase;

    private Connection(String serverHost, int serverPort, String username, String password, String privateKey, String passphrase) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.username = username;
        this.password = password;
        this.privateKey = privateKey;
        this.passphrase = passphrase;
    }

    public static Connection from(Target target) {
        guardClause(target);

        final String host = target.host();
        final int port = extractPort(target);
        final String username = target.user().orElse(EMPTY);
        final String password = target.userPassword().orElse(EMPTY);
        final String privateKey = target.privateKey().orElse(EMPTY);
        final String passphrase = target.privateKeyPassword().orElse(EMPTY);

        return new Connection(host, port, username, password, privateKey, passphrase);
    }

    public static Optional<Connection> proxyFrom(Target target) {
        return target.property("proxy").map(proxy -> {
            try {
                URI proxyUri = new URI(proxy);
                final String proxyHost = proxyUri.getHost();
                final int proxyPort = proxyUri.getPort() == -1 ? 22 : proxyUri.getPort();
                final String proxyUsername = target.property("proxyUser").orElse(EMPTY);
                final String proxyPassword = target.property("proxyPassword").orElse(EMPTY);
                final String proxyPrivateKey = target.property("proxyPrivateKey").orElse(EMPTY);
                final String proxyPassphrase = target.property("proxyPassphrase").orElse(EMPTY);

                return new Connection(proxyHost, proxyPort, proxyUsername, proxyPassword, proxyPrivateKey, proxyPassphrase);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public boolean usePrivateKey() {
        return isNotBlank(privateKey);
    }

    private static void guardClause(Target target) {
        if (target.uri() == null) {
            throw new IllegalArgumentException("Target URL is undefined");
        }
        if (target.host() == null || target.host().isEmpty()) {
            throw new IllegalArgumentException("Target is badly defined");
        }
    }

    private static int extractPort(Target target) {
        int serverPort = target.port();
        return serverPort == -1 ? 22 : serverPort;
    }
}

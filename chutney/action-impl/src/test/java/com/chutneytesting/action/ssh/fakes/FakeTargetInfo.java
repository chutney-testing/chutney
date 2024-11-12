/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.ssh.fakes;

import static java.util.Optional.ofNullable;

import com.chutneytesting.action.spi.injectable.Target;
import java.util.HashMap;
import java.util.Map;
import org.apache.sshd.server.SshServer;

public class FakeTargetInfo {

    private static final String RSA_PRIVATE_KEY = FakeTargetInfo.class.getResource("/security/client_rsa.key").getPath();
    private static final String ECDSA_PRIVATE_KEY = FakeTargetInfo.class.getResource("/security/client_ecdsa.key").getPath();

    public static Target buildTargetWithPassword(SshServer sshServer) {
        return buildTarget(sshServer, FakeServerSsh.PASSWORD, null, null, null, null, null);
    }

    public static Target buildTargetWithPrivateKeyWithoutPassphrase(SshServer sshServer) {
        return buildTarget(sshServer, null, RSA_PRIVATE_KEY, null, null, null, null);
    }

    public static Target buildTargetWithPrivateKeyWithPassphrase(SshServer sshServer) {
        return buildTarget(sshServer, null, ECDSA_PRIVATE_KEY, "password", null, null, null);
    }

    public static Target buildTargetWithPassword(SshServer sshServer, String proxy, String proxyUser, String proxyPassword) {
        return buildTarget(sshServer, FakeServerSsh.PASSWORD, null, null, proxy, proxyUser, proxyPassword);
    }

    private static Target buildTarget(
        SshServer sshServer,
        String userPassword,
        String privateKeyPath,
        String privateKeyPassphrase,
        String proxy,
        String proxyUser,
        String proxyPassword
    ) {
        Map<String, String> properties = new HashMap<>();
        properties.put("user", FakeServerSsh.USERNAME);
        ofNullable(userPassword).ifPresent(cp -> properties.put("password", cp));
        ofNullable(privateKeyPath).ifPresent(pkp -> properties.put("privateKey", pkp));
        ofNullable(privateKeyPassphrase).ifPresent(pkp -> properties.put("privateKeyPassphrase", pkp));
        ofNullable(proxy).ifPresent(pkp -> properties.put("proxy", pkp));
        ofNullable(proxyUser).ifPresent(pkp -> properties.put("proxyUser", pkp));
        ofNullable(proxyPassword).ifPresent(pkp -> properties.put("proxyPassword", pkp));
        return new HardcodedTarget(sshServer, properties);
    }
}

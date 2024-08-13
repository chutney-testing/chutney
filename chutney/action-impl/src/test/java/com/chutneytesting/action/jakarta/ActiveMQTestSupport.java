/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */


package com.chutneytesting.action.jakarta;

import com.chutneytesting.tools.SocketUtils;
import org.apache.activemq.artemis.core.config.impl.ConfigurationImpl;
import org.apache.activemq.artemis.core.server.ActiveMQServer;
import org.apache.activemq.artemis.core.server.ActiveMQServers;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

public class ActiveMQTestSupport {

    private static ActiveMQServer server;
    private static int serverPort;

    static String keyStorePath = ActiveMQTestSupport.class.getResource("/security/server.jks").getPath().toString();
    static String keyStorePassword = "server";

    static String trustStorePath = ActiveMQTestSupport.class.getResource("/security/truststore.jks").getPath().toString();
    static String trustStorePassword = "truststore";

    @BeforeAll
    public static void setUp() throws Exception {
        serverPort = SocketUtils.freePortFromSystem();
        server = ActiveMQServers.newActiveMQServer(new ConfigurationImpl()
            .setPersistenceEnabled(false)
            .setJournalDirectory("target/data/journal")
            .setSecurityEnabled(false)
            .addAcceptorConfiguration("ssl",
                "tcp://localhost:" + serverPort + "?" +
                    "sslEnabled=true" +
                    "&keyStorePath=" + keyStorePath +
                    "&keyStorePassword=" + keyStorePassword +
                    "&trustStorePath=" + trustStorePath +
                    "&trustStorePassword" + trustStorePassword +
                    "&needClientAuth=false"
                // "&wantClientAuth=true" certificate must either be marked as having both clientAuth and serverAuth extended key usage
            ));
        server.start();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        server.stop();
    }

    protected String serverUri() {
        return "tcp://localhost:" + serverPort;
    }
}


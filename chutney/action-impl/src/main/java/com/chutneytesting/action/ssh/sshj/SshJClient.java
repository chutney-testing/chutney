/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.ssh.sshj;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.apache.commons.lang3.StringUtils.isBlank;

import com.chutneytesting.action.spi.injectable.Logger;
import com.chutneytesting.action.ssh.Connection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.security.PublicKey;
import java.util.List;
import java.util.Optional;
import net.schmizz.concurrent.Event;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.common.LoggerFactory;
import net.schmizz.sshj.common.StreamCopier;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.HostKeyVerifier;
import net.schmizz.sshj.userauth.UserAuthException;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;

public class SshJClient implements SshClient {

    private final Connection connection;
    private final Connection proxyConnection;
    private final Logger logger;
    private final boolean shell;

    public SshJClient(Connection connection, Connection proxyConnection, boolean shell, Logger logger) {
        this.connection = connection;
        this.proxyConnection = proxyConnection;
        this.logger = logger;
        this.shell = shell;
    }

    @Override
    public CommandResult execute(Command command) throws IOException {
        SSHClient sshClient = new SSHClient();
        Optional<SSHClient> tunnel = connect(sshClient);
        try {
            authenticate(sshClient, connection);
            return executeCommand(sshClient, command);
        } finally {
            sshClient.disconnect();
            if (tunnel.isPresent()) {
                tunnel.get().disconnect();
            }
        }
    }

    private Optional<SSHClient> connect(SSHClient client) throws IOException {
        client.addHostKeyVerifier(alwaysVerified()); // TODO : Add best way host key verifier to really check.
        Optional<SSHClient> tunnel = tunnel();
        if (tunnel.isPresent()) {
            client.connectVia(tunnel.get().newDirectConnection(connection.serverHost, connection.serverPort));
        } else {
            client.connect(connection.serverHost, connection.serverPort);
        }
        return tunnel;
    }

    private Optional<SSHClient> tunnel() {
        return ofNullable(proxyConnection).map(pc -> {
            SSHClient sshClient = new SSHClient();
            try {
                sshClient.addHostKeyVerifier(alwaysVerified()); // TODO : Add best way host key verifier to really check.
                sshClient.connect(pc.serverHost, pc.serverPort);
                authenticate(sshClient, pc);
            } catch (IOException e) {
                logger.error("Error in proxy setup : " + e.getMessage());
            }
            return sshClient;
        });
    }

    private void authenticate(SSHClient client, Connection connection) throws IOException {
        if (isBlank(connection.privateKey)) {
            logger.info("Authentication via username/password as " + connection.username);
            loginWithPassword(client, connection.username, connection.password);
        } else {
            logger.info("Authentication via private key as " + connection.username);
            loginWithPrivateKey(client, connection.username, connection.privateKey, connection.passphrase);
        }
    }

    private void loginWithPassword(SSHClient client, String username, String password) throws UserAuthException, TransportException {
        client.authPassword(username, password);
    }

    private void loginWithPrivateKey(SSHClient client, String username, String privateKey, String passphrase) throws IOException {
        KeyProvider keyProvider = client.loadKeys(privateKey, passphrase);
        client.authPublickey(username, keyProvider);
    }

    private CommandResult executeCommand(SSHClient sshClient, Command command) throws IOException {
        try (Session session = sshClient.startSession()) {
            if (shell) {
                return shellCommand(command, session);
            } else {
                return execCommand(command, session);
            }
        }
    }

    private CommandResult shellCommand(Command command, Session session) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayOutputStream err = new ByteArrayOutputStream();

        session.allocateDefaultPTY();
        Session.Shell shell = session.startShell();

        Event<IOException> outDone = new StreamCopier(shell.getInputStream(), out, LoggerFactory.DEFAULT)
            .bufSize(shell.getLocalMaxPacketSize())
            .spawn("out");

        Event<IOException> errDone = new StreamCopier(shell.getErrorStream(), err, LoggerFactory.DEFAULT)
            .bufSize(shell.getLocalMaxPacketSize())
            .spawn("err");

        PrintWriter shellOut = new PrintWriter(shell.getOutputStream());
        command.command.lines().forEach(cmdLine -> {
            shellOut.print(cmdLine);
            shellOut.println();
            shellOut.flush();
        });

        long cmdTimeout = command.timeout.toMilliseconds() * command.command.lines().toList().size();
        outDone.await(cmdTimeout, MILLISECONDS);
        errDone.await(cmdTimeout, MILLISECONDS);

        return new CommandResult(
            command,
            err.size() > 0 ? -1 : 0,
            cleanShellOutput(out.toString()).replaceAll("\r", ""),
            cleanShellOutput(err.toString()).replaceAll("\r", "")
        );
    }

    private CommandResult execCommand(Command command, Session session) throws IOException {
        Session.Command sshJCommand = session.exec(command.command);
        sshJCommand.join(command.timeout.toMilliseconds(), MILLISECONDS);

        try (InputStream is = sshJCommand.getInputStream();
             InputStream es = sshJCommand.getErrorStream()) {
            return new CommandResult(command,
                sshJCommand.getExitStatus(),
                readInputStream(is),
                readInputStream(es));
        }
    }

    private String readInputStream(InputStream inputStream) throws IOException {
        return IOUtils.readFully(inputStream).toString().replaceAll("\r", "");
    }

    private static HostKeyVerifier alwaysVerified() {
        return new HostKeyVerifier() {
            @Override
            public boolean verify(String hostname, int port, PublicKey key) {
                return true;
            }

            @Override
            public List<String> findExistingAlgorithms(String hostname, int port) {
                return emptyList();
            }
        };
    }

    private static final String BRACKETED_PASTE_ON = "\033[?2004h";
    private static final String BRACKETED_PASTE_OFF = "\033[?2004l";
    private static final String BRACKETED_PASTE_BEGIN = "\033[200~";
    private static final String BRACKETED_PASTE_END = "\033[201~";
    private static final String ESCAPE_SEQUENCE = "\\e\\[[\\d;]*[^\\d;]";

    private static String cleanShellOutput(String output) {
        return output
            .replace(BRACKETED_PASTE_ON, "")
            .replace(BRACKETED_PASTE_OFF, "")
            .replace(BRACKETED_PASTE_BEGIN, "")
            .replace(BRACKETED_PASTE_END, "")
            .replaceAll(ESCAPE_SEQUENCE, "");
    }
}

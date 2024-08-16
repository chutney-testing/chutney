/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.ssh.sshd;

import java.io.IOException;
import org.apache.sshd.server.channel.ChannelSession;
import org.apache.sshd.server.command.Command;
import org.apache.sshd.server.shell.ShellFactory;

public class NoShellFactory implements ShellFactory {

    private final SshServerMock sshServerMock;

    public NoShellFactory(SshServerMock sshServerMock) {
        this.sshServerMock = sshServerMock;
    }

    @Override
    public Command createShell(ChannelSession channel) throws IOException {
        return new com.chutneytesting.action.ssh.sshd.Command(sshServerMock);
    }
}

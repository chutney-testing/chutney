/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.ssh.sshj;

import java.io.IOException;

public interface SshClient {
    CommandResult execute(Command command) throws IOException;
}

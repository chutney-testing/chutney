/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.ssh.scp;

import java.io.IOException;

public interface ScpClient extends AutoCloseable {

    void upload(String local, String remote) throws IOException;

    void download(String remote, String local) throws IOException;

}

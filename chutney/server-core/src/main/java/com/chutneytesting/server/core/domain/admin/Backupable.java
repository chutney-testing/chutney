/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.admin;

import java.io.OutputStream;

public interface Backupable {
    void backup(OutputStream outputStream);

    String name();
}

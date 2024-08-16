/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.admin.api.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class BackupDto {

    private final DateTimeFormatter backupIdTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private final LocalDateTime time;

    private final List<String> Backupables;

    public BackupDto(LocalDateTime time, List<String> backupables) {
        this.time = time;
        Backupables = backupables;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public List<String> getBackupables() {
        return Backupables;
    }

    public String getId() {
        return this.time.format(backupIdTimeFormatter);
    }
}

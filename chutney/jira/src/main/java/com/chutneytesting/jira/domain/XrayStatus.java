/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.jira.domain;

public enum XrayStatus {
    PASS("PASS"),
    FAIL("FAIL");

    public final String value;

    XrayStatus(String value) {
        this.value = value;
    }
}

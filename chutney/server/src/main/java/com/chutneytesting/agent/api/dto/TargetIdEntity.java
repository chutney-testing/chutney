/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.agent.api.dto;

public class TargetIdEntity {
    public final String name;
    public final String environment;

    public TargetIdEntity(String name, String environment) {
        this.name = name;
        this.environment = environment;
    }
}

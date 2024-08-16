/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.http.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.tomakehurst.wiremock.WireMockServer;

public class WiremockModule extends SimpleModule {

    private static final String NAME = "ChutneyWiremockModule";

    public WiremockModule() {
        super(NAME);
        addSerializer(WireMockServer.class, new WireMockServerSerializer());
    }
}

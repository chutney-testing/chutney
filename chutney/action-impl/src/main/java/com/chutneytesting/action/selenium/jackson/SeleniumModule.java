/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.selenium.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.openqa.selenium.WebDriver;

public class SeleniumModule extends SimpleModule {

    private static final String NAME = "ChutneySeleniumModule";

    public SeleniumModule() {
        super(NAME);
        addSerializer(WebDriver.class, new WebDriverSerializer());
    }
}

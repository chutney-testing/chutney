/*
 * Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chutneytesting.action.selenium;

import static com.chutneytesting.action.selenium.parameter.SeleniumActionActionParameter.WEBDRIVER;

import com.chutneytesting.action.spi.ActionExecutionResult;
import com.chutneytesting.action.spi.injectable.Input;
import com.chutneytesting.action.spi.injectable.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class SeleniumScreenShotAction extends SeleniumAction {

    public SeleniumScreenShotAction(Logger logger,
                                  @Input(WEBDRIVER) WebDriver webDriver) {
        super(logger, webDriver);
    }

    @Override
    public ActionExecutionResult executeSeleniumAction() {
        String screenShot = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BASE64);
        logger.reportOnly().info("data:image/png;base64," + screenShot);

        return ActionExecutionResult.ok();
    }
}

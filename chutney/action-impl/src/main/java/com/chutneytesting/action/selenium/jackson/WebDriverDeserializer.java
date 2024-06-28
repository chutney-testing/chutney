/*
 * Copyright 2017-2023 Enedis
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

package com.chutneytesting.action.selenium.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class WebDriverDeserializer extends StdDeserializer<WebDriver> {

    protected WebDriverDeserializer() {
        super(WebDriver.class);
    }

    @Override
    public WebDriver deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        JsonNode node = p.getCodec().readTree(p);
        if (!node.has("driver")) {
            throw new IllegalArgumentException(); // TODO
        }
        String driver = node.get("driver").asText();
        String capabilitiesString = node.get("capabilities").asText();
        Capabilities capabilities = new ObjectMapper().readValue(capabilitiesString, Capabilities.class);
        if (driver.contains("firefox")) {
            FirefoxOptions firefoxOptions = new FirefoxOptions(capabilities);
            return new FirefoxDriver(firefoxOptions);
        } else if (driver.contains("chrome")) {
            ChromeOptions chromeOptions = new ChromeOptions().merge(capabilities);
            return new ChromeDriver(chromeOptions);
        }
        throw new IllegalArgumentException(); // TODO
    }
}

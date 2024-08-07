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

package com.chutneytesting.admin.api;

import static com.chutneytesting.ServerConfigurationValues.SERVER_INSTANCE_NAME_VALUE;
import static com.chutneytesting.admin.api.InfoController.BASE_URL;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BASE_URL)
@CrossOrigin(origins = "*")
public class InfoController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfoController.class);
    public static final String BASE_URL = "/api/v1/info";
    private final Properties buildProperties;

    @Value(SERVER_INSTANCE_NAME_VALUE)
    private String applicationName;

    public InfoController() {
        buildProperties = loadBuildProperties();
    }

    private Properties loadBuildProperties() {
        Properties props = new Properties();
        try (InputStream input = InfoController.class.getClassLoader().getResourceAsStream("META-INF/build.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException ioe) {
            LOGGER.warn("Cannot read build properties file", ioe);
        }
        return props;
    }

    @GetMapping(path = "/build/version")
    public String buildVersion() {
        return buildProperties.getProperty("build.version", "");
    }

    @GetMapping(path = "/appname")
    public String applicationName() {
        return applicationName;
    }
}

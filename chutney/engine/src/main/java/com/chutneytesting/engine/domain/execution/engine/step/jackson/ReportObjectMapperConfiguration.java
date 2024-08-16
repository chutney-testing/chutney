/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.execution.engine.step.jackson;

import com.chutneytesting.tools.MyMixInForIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.jdom2.Element;
import org.springframework.core.io.Resource;

public class ReportObjectMapperConfiguration {

    private static final ObjectMapper reportObjectMapper = configureObjectMapper();

    public static ObjectMapper reportObjectMapper() {
        return reportObjectMapper;
    }

    private static ObjectMapper configureObjectMapper() {
        SimpleModule jdomElementModule = new SimpleModule();
        jdomElementModule.addSerializer(Element.class, new JDomElementSerializer());
        return new ObjectMapper()
            .addMixIn(Resource.class, MyMixInForIgnoreType.class)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .registerModule(jdomElementModule)
            .findAndRegisterModules();
    }
}

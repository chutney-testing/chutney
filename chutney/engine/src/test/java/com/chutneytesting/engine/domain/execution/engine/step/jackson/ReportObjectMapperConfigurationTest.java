/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.execution.engine.step.jackson;


import static org.assertj.core.api.Assertions.assertThatCode;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jdom2.Element;
import org.junit.jupiter.api.Test;

class ReportObjectMapperConfigurationTest {
    @Test
    void verify_no_infinite_recursion_when_serializing_jdom2_element() {
        ObjectMapper reportObjectMapper = ReportObjectMapperConfiguration.reportObjectMapper();

        Element result = new Element("node1")
            .addContent(new Element("node2").setAttribute("attr", "val"))
            .getChild("node2");

        assertThatCode(() -> reportObjectMapper.writeValueAsString(result)).doesNotThrowAnyException();
    }
}

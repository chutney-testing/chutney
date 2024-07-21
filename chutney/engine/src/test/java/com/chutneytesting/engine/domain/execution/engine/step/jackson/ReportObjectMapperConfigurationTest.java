/*
 *  Copyright 2017-2023 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

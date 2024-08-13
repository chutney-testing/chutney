/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.execution.engine.step.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.io.Serial;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class JDomElementSerializer extends StdSerializer<Element> {

    @Serial
    private static final long serialVersionUID = 1L;

    public JDomElementSerializer() {
        this(null);
    }

    protected JDomElementSerializer(Class<Element> t) {
        super(t);
    }

    @Override
    public void serialize(Element element, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        String xmlString = new XMLOutputter(Format.getCompactFormat()).outputString(element);
        jsonGenerator.writeObject(xmlString);
    }
}

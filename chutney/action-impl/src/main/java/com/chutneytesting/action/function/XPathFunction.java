/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.function;

import com.chutneytesting.action.common.XmlUtils;
import com.chutneytesting.action.spi.SpelFunction;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom2.Attribute;
import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Text;
import org.jdom2.filter.ContentFilter;
import org.jdom2.filter.Filter;
import org.jdom2.xpath.XPathExpression;

public class XPathFunction {

    @SpelFunction
    public static Object xpath(String documentAsString, String xpath) throws XmlUtils.InvalidXmlDocumentException, XmlUtils.InvalidXPathException {
        return xpathNs(documentAsString, xpath, new HashMap<>());
    }

    @SpelFunction
    public static Object xpathNs(String documentAsString, String xpath, Map<String, String> nsPrefixes) throws XmlUtils.InvalidXmlDocumentException, XmlUtils.InvalidXPathException {
        Document document = XmlUtils.toDocument(documentAsString);
        XPathExpression<Object> xpathExpression = XmlUtils.compileXPath(xpath, nsPrefixes);
        Object jDomObject = xpathExpression.evaluateFirst(document);
        return unwrapJdomSimpleObject(jDomObject);
    }

    private static Object unwrapJdomSimpleObject(Object jDomObject) {
        final Object result;
        if (jDomObject instanceof Text text) {
            result = text.getText();
        } else if(jDomObject instanceof Attribute attribute) {
            result = attribute.getValue();
        } else if (jDomObject instanceof Element element) {
            result = unwrapJdomElement(element);
        } else {
            result = jDomObject;
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private static Object unwrapJdomElement(Element jDomObject) {
        final Object result;
        List<Content> cdata = jDomObject.getContent(new ContentFilter(ContentFilter.CDATA));
        List<Content> contents = jDomObject.getContent((Filter<Content>) new ContentFilter(ContentFilter.COMMENT).negate());
        // It is necessary to filter on CDATA first as ancestor may contain three children
        // if document is not inlined :
        // TextNode \n
        // CDATA
        // TextNode \n
        if (cdata.size() == 1) {
            result = unwrapJdomSimpleObject(cdata.get(0));
        } else if (contents.size() == 1) {
            result = unwrapJdomSimpleObject(contents.get(0));
        } else {
            result = jDomObject;
        }
        return result;
    }
}

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

package com.chutneytesting.action.jakarta.consumer.bodySelector;

import com.chutneytesting.action.common.XmlUtils;
import com.chutneytesting.action.jakarta.domain.XmlContent;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

class XpathBodySelectorParser implements BodySelectorParser {

    private static final String BODY_SELECTOR_REGEX = "^XPATH '(?<xpath>.+)'$";
    private static final Pattern BODY_SELECTOR_PATTERN = Pattern.compile(BODY_SELECTOR_REGEX);

    @Override
    public String description() {
        return "XPath selector: " + BODY_SELECTOR_REGEX;
    }

    /**
     * @throws IllegalArgumentException when given XPATH does not compile
     */
    @Override
    public Optional<BodySelector> tryParse(String selector) throws IllegalArgumentException {
        Matcher matcher = BODY_SELECTOR_PATTERN.matcher(selector);
        final Optional<BodySelector> optionalBodySelector;
        if (matcher.matches()) {
            String xpath = matcher.group("xpath");
            XPathExpression<Boolean> xPathExpression = XPathFactory.instance().compile(xpath, Filters.fboolean());
            optionalBodySelector = Optional.of(new XpathBodySelector(xPathExpression));
        } else {
            optionalBodySelector = Optional.empty();
        }
        return optionalBodySelector;
    }

    private static class XpathBodySelector extends TextMessageBodySelector {
        private final XPathExpression<Boolean> xPathExpression;
        private final SAXBuilder saxBuilder = XmlUtils.saxBuilder();

        XpathBodySelector(XPathExpression<Boolean> xPathExpression) {
            this.xPathExpression = xPathExpression;
        }

        @Override
        public boolean match(String messageBody) {
            XmlContent xmlContent = new XmlContent(saxBuilder, messageBody);
            return xmlContent
                .tryBuildDocumentWithoutNamespaces()
                .map(xPathExpression::evaluateFirst)
                .orElse(Boolean.FALSE);
        }
    }
}

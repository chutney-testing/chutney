/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.jakarta.consumer;

import com.chutneytesting.action.jakarta.consumer.bodySelector.BodySelector;
import com.chutneytesting.tools.Streams;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.QueueBrowser;
import java.util.Enumeration;
import java.util.Optional;

class SelectedMessageConsumer implements Consumer {

    private final QueueBrowser browser;
    private final BodySelector bodySelector;
    private final int browserMaxDepth;

    SelectedMessageConsumer(QueueBrowser browser, BodySelector bodySelector, int browserMaxDepth) {
        this.browser = browser;
        this.bodySelector = bodySelector;
        this.browserMaxDepth = browserMaxDepth;
    }

    @SuppressWarnings("unchecked")
    public Optional<Message> getMessage() throws JMSException {
        Enumeration<Message> messageEnumeration = browser.getEnumeration();
        return Streams.toStream(messageEnumeration)
            .limit(browserMaxDepth)
            .filter(bodySelector::match)
            .findFirst();
    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.kafka;

import com.chutneytesting.action.spi.injectable.Logger;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.KafkaUtils;

public class ListenerContainerErrorHandler extends DefaultErrorHandler {
    private final Logger logger;
    private final Set<String> errors = new HashSet<>();

    public ListenerContainerErrorHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void handleRemaining(Exception thrownException, List<ConsumerRecord<?, ?>> records, Consumer<?, ?> consumer, MessageListenerContainer container) {
        logger.error("Error occurred while processing " + KafkaUtils.format(records.get(0)) + " : " + thrownException.getCause());
        super.handleRemaining(thrownException, records, consumer, container);
    }

    @Override
    public void handleOtherException(Exception thrownException, Consumer<?, ?> consumer, MessageListenerContainer container, boolean batchListener) {
        if (!errors.contains(thrownException.getMessage())) {
            errors.add(thrownException.getMessage());
            logger.error(thrownException.getMessage());
        }
        super.handleOtherException(thrownException, consumer, container, batchListener);
    }
}

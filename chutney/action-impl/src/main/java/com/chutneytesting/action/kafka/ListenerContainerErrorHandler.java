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

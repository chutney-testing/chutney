/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.kafka;

import java.util.Collection;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;

public class CustomConsumerRebalanceListener implements ConsumerAwareRebalanceListener {
    @Override
    public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        ConsumerAwareRebalanceListener.super.onPartitionsAssigned(consumer, partitions);
        consumer.seekToBeginning(partitions);
    }
}

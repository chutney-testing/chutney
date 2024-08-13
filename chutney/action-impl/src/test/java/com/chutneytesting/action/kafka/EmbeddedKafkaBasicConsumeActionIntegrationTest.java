/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.kafka;

import org.junit.jupiter.api.AfterAll;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.EmbeddedKafkaZKBroker;

public class EmbeddedKafkaBasicConsumeActionIntegrationTest extends KafkaBasicConsumeActionIntegrationTest {

    private static EmbeddedKafkaBroker embeddedKafkaBroker;
    @Override
    protected String initBroker() {
        embeddedKafkaBroker = new EmbeddedKafkaZKBroker(1);
        embeddedKafkaBroker.afterPropertiesSet();
        return embeddedKafkaBroker.getBrokersAsString();
    }

    @AfterAll
    public static void afterAll() {
        producer.close();
        embeddedKafkaBroker.destroy();
    }
}

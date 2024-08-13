/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.kafka.jackson;

import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

public class KafkaModule extends SimpleModule {

    private static final String NAME = "ChutneyKafkaModule";

    public KafkaModule() {
        super(NAME);
        addSerializer(EmbeddedKafkaBroker.class, new EmbeddedKafkaBrokerSerializer());
    }
}

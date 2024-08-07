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

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.kafka;

import static com.chutneytesting.action.kafka.KafkaClientFactoryHelper.resolveBootStrapServerConfig;
import static java.util.Collections.unmodifiableMap;
import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;
import static org.apache.kafka.clients.consumer.ConsumerConfig.GROUP_ID_CONFIG;

import com.chutneytesting.action.spi.injectable.Target;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

public class KafkaConsumerFactoryFactory {

    public ConsumerFactory<String, String> create(Target target, String group, Map<String, String> config) {

        Map<String, Object> consumerConfig = new HashMap<>();
        consumerConfig.put(BOOTSTRAP_SERVERS_CONFIG, resolveBootStrapServerConfig(target));
        consumerConfig.put(GROUP_ID_CONFIG, group);
        target.trustStore().ifPresent(trustStore -> {
            consumerConfig.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, trustStore);
            consumerConfig.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, target.trustStorePassword().orElseThrow(IllegalArgumentException::new));
        });

        consumerConfig.putAll(config);

        return new DefaultKafkaConsumerFactory<>(
            unmodifiableMap(consumerConfig),
            new StringDeserializer(),
            new StringDeserializer());
    }
}

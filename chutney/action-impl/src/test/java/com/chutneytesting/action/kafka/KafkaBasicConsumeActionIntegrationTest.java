/*
 * Copyright 2017-2023 Enedis
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

package com.chutneytesting.action.kafka;

import static com.chutneytesting.action.kafka.KafkaBasicConsumeAction.OUTPUT_BODY;
import static com.chutneytesting.action.kafka.KafkaBasicConsumeAction.OUTPUT_BODY_HEADERS_KEY;
import static com.chutneytesting.action.kafka.KafkaBasicConsumeAction.OUTPUT_BODY_PAYLOAD_KEY;
import static com.chutneytesting.action.kafka.KafkaBasicConsumeAction.OUTPUT_HEADERS;
import static com.chutneytesting.action.kafka.KafkaBasicConsumeAction.OUTPUT_PAYLOADS;
import static com.chutneytesting.action.spi.ActionExecutionResult.Status.Failure;
import static com.chutneytesting.action.spi.ActionExecutionResult.Status.Success;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.kafka.test.utils.KafkaTestUtils.getCurrentOffset;
import static org.springframework.util.MimeTypeUtils.TEXT_PLAIN_VALUE;

import com.chutneytesting.action.TestLogger;
import com.chutneytesting.action.TestTarget;
import com.chutneytesting.action.http.HttpsServerStartActionTest;
import com.chutneytesting.action.spi.Action;
import com.chutneytesting.action.spi.ActionExecutionResult;
import com.chutneytesting.action.spi.injectable.Target;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.IntegerSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaZKBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

public class KafkaBasicConsumeActionIntegrationTest {

    private static final String TOPIC = "topic";
    private static final String GROUP = "mygroup";
    private static String KEYSTORE_JKS;
    private EmbeddedKafkaZKBroker embeddedKafkaBroker;

    private Producer<Integer, String> producer;
    private TestTarget.TestTargetBuilder targetBuilder;

    private TestLogger logger;

    @BeforeAll
    static void beforeAll() throws URISyntaxException {
        KEYSTORE_JKS = Paths.get(requireNonNull(HttpsServerStartActionTest.class.getResource("/security/server.jks")).toURI()).toAbsolutePath().toString();
    }

    @BeforeEach
    public void before() {
        logger = new TestLogger();
        embeddedKafkaBroker = new EmbeddedKafkaZKBroker(1, false, TOPIC);
        embeddedKafkaBroker.afterPropertiesSet();
        producer = createProducer();
        targetBuilder = TestTarget.TestTargetBuilder.builder().withTargetId("kafka").withUrl("tcp://" + embeddedKafkaBroker.getBrokersAsString());
    }

    @AfterEach
    void tearDown() {
        producer.close();
        embeddedKafkaBroker.destroy();
    }

    @Test
    public void should_consume_message_from_broker_without_truststore() {

        // given
        producer.send(new ProducerRecord<>(TOPIC, 123, "my-test-value"));

        Map<String, String> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.name().toLowerCase());

        Action sut = new KafkaBasicConsumeAction(targetBuilder.build(), TOPIC, GROUP, props, 1, null, null, TEXT_PLAIN_VALUE, "10 s", null, null, logger);


        // when
        ActionExecutionResult actionExecutionResult = sut.execute();

        // then
        assertThat(actionExecutionResult.status).isEqualTo(Success);
        List<Map<String, Object>> body = assertActionOutputsSize(actionExecutionResult, 1);
        assertThat(body.get(0).get("payload")).isEqualTo("my-test-value");
    }

    @Test
    public void consumer_from_target_with_truststore_should_reject_ssl_connection_with_broker_without_truststore_configured() {
        // given
        Target target = targetBuilder.withProperty("trustStore", KEYSTORE_JKS)
            .withProperty("trustStorePassword", "server")
            .withProperty("security.protocol", "SSL")
            .build();

        Map<String, String> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);

        Action sut = new KafkaBasicConsumeAction(target, TOPIC, GROUP, props, 1, null, null, TEXT_PLAIN_VALUE, "3000 ms", null, null,logger);

        // when
        ActionExecutionResult actionExecutionResult = sut.execute();

        // then
        assertThat(actionExecutionResult.status).isEqualTo(Failure);

    }

    @Test
    public void should_reset_offset_to_the_beginning() {
        // given
        Map<String, String> props = new HashMap<>();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OffsetResetStrategy.EARLIEST.name().toLowerCase());

        Action sut = new KafkaBasicConsumeAction(targetBuilder.build(), TOPIC, GROUP, props, 1, null, null, TEXT_PLAIN_VALUE, "3000 ms", null,null, logger);

        producer.send(new ProducerRecord<>(TOPIC, 123, "1"));


        ActionExecutionResult actionExecutionResult = sut.execute();
        assertThat(actionExecutionResult.status).isEqualTo(Success);
        List<Map<String, Object>> body = assertActionOutputsSize(actionExecutionResult, 1);
        assertThat(body.get(0).get("payload")).isEqualTo("1");

        // second time
        sut = new KafkaBasicConsumeAction(targetBuilder.build(), TOPIC, GROUP, props, 1, null, null, TEXT_PLAIN_VALUE, "3000 ms", null, null,logger);
        actionExecutionResult = sut.execute();

        assertThat(actionExecutionResult.status).isEqualTo(Failure);

        // third time with reset
        sut = new KafkaBasicConsumeAction(targetBuilder.build(), TOPIC, GROUP, props, 1, null, null, TEXT_PLAIN_VALUE, "3000 ms", null, true,logger);
        actionExecutionResult = sut.execute();

        assertThat(actionExecutionResult.status).isEqualTo(Success);
        body = assertActionOutputsSize(actionExecutionResult, 1);
        assertThat(body.get(0).get("payload")).isEqualTo("1");

        // third time without reset
        sut = new KafkaBasicConsumeAction(targetBuilder.build(), TOPIC, GROUP, props, 1, null, null, TEXT_PLAIN_VALUE, "3000 ms", null,null, logger);
        actionExecutionResult = sut.execute();

        assertThat(actionExecutionResult.status).isEqualTo(Failure);

    }

    private List<Map<String, Object>> assertActionOutputsSize(ActionExecutionResult actionExecutionResult, int size) {
        assertThat(actionExecutionResult.outputs).hasSize(3);

        final List<Map<String, Object>> body = (List<Map<String, Object>>) actionExecutionResult.outputs.get(OUTPUT_BODY);
        final List<Map<String, Object>> payloads = (List<Map<String, Object>>) actionExecutionResult.outputs.get(OUTPUT_PAYLOADS);
        final List<Map<String, Object>> headers = (List<Map<String, Object>>) actionExecutionResult.outputs.get(OUTPUT_HEADERS);
        assertThat(body).hasSize(size);
        assertThat(payloads).hasSize(size);
        assertThat(headers).hasSize(size);

        Map<String, Object> bodyTmp;
        for (int i = 0; i < body.size(); i++) {
            bodyTmp = body.get(i);
            assertThat(bodyTmp.get(OUTPUT_BODY_PAYLOAD_KEY)).isEqualTo(payloads.get(i));
            assertThat(bodyTmp.get(OUTPUT_BODY_HEADERS_KEY)).isEqualTo(headers.get(i));
        }

        return body;
    }

    private Producer<Integer, String> createProducer() {
        Map<String, Object> producerProps = new HashMap<>(KafkaTestUtils.producerProps(embeddedKafkaBroker));
        return new DefaultKafkaProducerFactory<>(producerProps, new IntegerSerializer(), new StringSerializer()).createProducer();
    }
}

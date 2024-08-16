/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.kafka;

import static com.chutneytesting.action.spi.ActionExecutionResult.Status.Success;
import static java.util.Collections.emptyMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import com.chutneytesting.action.TestLogger;
import com.chutneytesting.action.spi.ActionExecutionResult;
import com.chutneytesting.action.spi.injectable.FinallyActionRegistry;
import com.chutneytesting.action.spi.injectable.Logger;
import com.chutneytesting.tools.SocketUtils;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

class KafkaBrokerStartActionTest {

    private final String topic = "topic";
    private final int kafkaPort = SocketUtils.findAvailableTcpPort();
    private final FinallyActionRegistry finallyActionRegistry = Mockito.mock(FinallyActionRegistry.class);
    private final Logger logger = new TestLogger();
    EmbeddedKafkaBroker server = null;

    @BeforeEach
    void setUp() {
        reset(finallyActionRegistry);
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.destroy();
        }
    }

    @Test
    void should_start_kafka_server() {
        KafkaBrokerStartAction action = new KafkaBrokerStartAction(logger, finallyActionRegistry, String.valueOf(kafkaPort), List.of(topic), emptyMap());
        ActionExecutionResult result = action.execute();

        assertThat(result.status).isEqualTo(Success);
        assertThat(result.outputs.get("kafkaBroker")).isNotNull();
        verify(finallyActionRegistry).registerFinallyAction(any());
        server = (EmbeddedKafkaBroker) result.outputs.get("kafkaBroker");
        assertThat(server.getTopics()).contains(topic);
    }
}

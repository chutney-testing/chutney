package com.chutneytesting.example.kafka

import com.chutneytesting.example.scenario.kafka_scenario
import com.chutneytesting.kotlin.dsl.Environment
import com.chutneytesting.kotlin.launcher.Launcher
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.utility.DockerImageName


class KafkaTest {
    private val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"))

    @BeforeEach
    fun setUp() {
        kafkaContainer.start()
    }

    @AfterEach
    fun tearDown() {
        kafkaContainer.stop()
    }

    @Test
    fun `publish & consume kafka message`() {
        val env = Environment("Global", "") {
            Target {
                Name("target")
                Url(kafkaContainer.bootstrapServers)
                Properties("auto.offset.reset" to "earliest")
            }
        }

        Launcher().run(kafka_scenario, env)
    }
}

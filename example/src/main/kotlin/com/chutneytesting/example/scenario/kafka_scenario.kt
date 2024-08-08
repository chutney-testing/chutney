package com.chutneytesting.example.scenario

import com.chutneytesting.kotlin.dsl.*

val kafka_scenario = Scenario(title = "Kafka produce & consume scenario") {
    val kafkaTarget = "target"
    val topic = "messages"
    val payload = "Hello, Kafka!"
    val headers = mapOf("Content-Type" to "text/plain")

    When("When a message \"$payload\" is published to the topic \"$topic\"") {
        KafkaBasicPublishAction(
            target = kafkaTarget,
            topic = topic,
            payload = payload,
            headers = headers
        )
    }
    Then("Then the message is consumed from the topic \"$topic\"") {
        KafkaBasicConsumeAction(
            target = kafkaTarget,
            topic = topic,
            group = "myConsumerGroup",
            timeout = "2 s",
            validations = mapOf(
                "Only one message found" to "#body.size() == 1".elEval()
            )
        )
    }
    And("And the consumed message is equal to \"$payload\"") {
        AssertAction(
            asserts = listOf(
                "#headers.get(0).get('Content-Type').equals('${headers["Content-Type"]}')".elEval(),
                "#body.get(0).get('payload').equals('$payload')".elEval(),
                "#payloads.get(0).equals('$payload')".elEval(),
                "#payload.equals('$payload')".elEval()
            )
        )
    }
}

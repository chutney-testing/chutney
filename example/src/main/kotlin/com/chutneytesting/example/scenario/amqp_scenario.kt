package com.chutneytesting.example.scenario

import com.chutneytesting.kotlin.dsl.*

const val RABBITMQ_EXCHANGE = "my.exchange"
const val RABBITMQ_QUEUE = "my.queue"

val amqp_scenario = Scenario(title = "Publish - consume") {

    val title = "Castle in the Sky"
    val director = "Hayao Miyazaki"
    val category = "fantasy"

    When("I publish my favorite film") {
        publishFilm(title, director, category)
    }

    Then("I consume available films from my queue") {
        AmqpBasicConsumeAction(
            target = "RABBITMQ_TARGET",
            queueName = RABBITMQ_QUEUE,
            nbMessages = 1,
            selector = "\$..[?(\$.headers.category=='$category')]",
            timeout = "5 sec",
            ack = true
        )
    }

    And("I check that I got my favorite film") {
        AssertAction(
            asserts = listOf(
                "#headers.get(0).get('category').equals('$category')".elEval(),
                "#jsonPath(#payload, '\$.title').equals('$title')".elEval(),
                "#jsonPath(#payload, '\$.director').equals('$director')".elEval(),
            )
        )
    }

}

val amqp_scenario_2 = Scenario(title = "Publish - get") {

    val title = "Demon slayer"
    val director = "Koyoharu Gotôge"
    val category = "dark fantasy"

    When("I publish my favorite film") {
        publishFilm(title, director, category)
    }

    Then("I get 1 film from my queue") {
        AmqpBasicGetAction(
            target = "RABBITMQ_TARGET",
            queueName = RABBITMQ_QUEUE
        )
    }

    And("I check that I got my favorite film") {
        AssertAction(
            asserts = listOf(
                "#headers.get('category').equals('$category')".elEval(),
                "#jsonPath(#payload, '\$.title').equals('$title')".elEval(),
                "#jsonPath(#payload, '\$.director').equals('$director')".elEval(),
            )
        )
    }
}

private fun ChutneyStepBuilder.publishFilm(title: String, director: String, category: String) {
    AmqpBasicPublishAction(
        target = "RABBITMQ_TARGET",
        exchangeName = RABBITMQ_EXCHANGE,
        routingKey = "children.film",
        headers = mapOf(
            "category" to category,
        ),
        properties = mapOf(
            "content_type" to "application/json",
        ),
        payload = """
                {
                "title": "$title",
                "director": "$director"
                }
            """.trimIndent(),
    )
}

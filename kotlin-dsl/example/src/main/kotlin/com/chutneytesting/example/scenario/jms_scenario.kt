package com.chutneytesting.example.scenario

import com.chutneytesting.kotlin.dsl.*

const val ACTIVEMQ_TARGET_NAME = "ACTIVEMQ_TARGET"

const val FILMS_DESTINATION = "films"


var GRAVE_OF_THE_FIREFLIES = """
                {
                "title": "Grave of the Fireflies",
                "director": "Isao Takahata",
                "rating": "94",
                "category": "fiction"
                }
            """

val activemq_scenario = Scenario(title = "Films library") {
    Given ("A new fiction film is released") {
        JmsSenderAction(
            target = ACTIVEMQ_TARGET_NAME,
            destination = FILMS_DESTINATION,
            headers = mapOf(
                "category" to "fiction"
            ),
            payload =  GRAVE_OF_THE_FIREFLIES.trimIndent()

        )
    }

    When("I subscribe to receive fiction films") {
        JmsListenerAction(
            target = ACTIVEMQ_TARGET_NAME,
            destination = FILMS_DESTINATION,
            selector = "category = 'fiction'",
            outputs = mapOf(
                "title" to "jsonPath(#textMessage, '\$.title')".spEL(),
                "rating" to "jsonPath(#textMessage, '\$.rating')".spEL()
            )
        )
    }

    Then ("I check that got the new film") {
        AssertAction(
            asserts = listOf(
                "title.equals('Grave of the Fireflies')".spEL(),
                "rating.equals(\"94\")".spEL(),
            )
        )
    }
}

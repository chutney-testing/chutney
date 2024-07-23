package com.chutneytesting.example.scenario

import com.chutneytesting.kotlin.dsl.*

const val HTTP_TARGET_NAME = "HTTP_TARGET"

const val FILMS_ENDPOINT = "/films"
private val JSON_CONTENT_TYPE = "Content-Type" to "application/json";

var FILM = """
                {
                "title": "Castle in the Sky",
                "director": "Hayao Miyazaki",
                "rating": "%rating%",
                "category": "fiction"
                }
            """


val http_scenario = Scenario(title = "Films library") {
    Given("I save a new film") {
        HttpPostAction(
            target = HTTP_TARGET_NAME,
            uri = FILMS_ENDPOINT,
            body = FILM.trimIndent(),
            headers = mapOf(
                JSON_CONTENT_TYPE
            ),
            validations = mapOf(
                statusValidation(201)
            ),
            outputs = mapOf(
                "filmId" to "#body".elEval()
            )
        )
    }

    When ("I update rating") {
        HttpPatchAction(
            target = HTTP_TARGET_NAME,
            uri = "$FILMS_ENDPOINT/\${#filmId}",
            body = """
                {
                "rating": "79",
                }
            """.trimIndent(),
            headers = mapOf(
                JSON_CONTENT_TYPE
            ),
            validations = mapOf(
                statusValidation(200)
            )
        )
    }

    Then ("I check that rating was updated") {
        Step("I get film by id") {
            HttpGetAction(
                target = HTTP_TARGET_NAME,
                uri = "$FILMS_ENDPOINT/\${#filmId}",
                headers = mapOf(
                    JSON_CONTENT_TYPE
                ),
                validations = mapOf(
                    statusValidation(200)
                ),
                outputs = mapOf(
                    "title" to "jsonPath(#body, '\$.title')".spEL(),
                    "rating" to "jsonPath(#body, '\$.rating')".spEL()
                )
            )
        }
        Step ("I check rating"){
            AssertAction(
                asserts = listOf(
                    "title.equals('Castle in the Sky')".spEL(),
                    "rating.equals(\"79\")".spEL()
                )
            )
        }
    }
}

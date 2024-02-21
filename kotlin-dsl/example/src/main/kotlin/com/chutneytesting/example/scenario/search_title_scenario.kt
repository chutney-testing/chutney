package com.chutneytesting.example.scenario

import com.chutneytesting.kotlin.dsl.AssertAction
import com.chutneytesting.kotlin.dsl.HttpGetAction
import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.spEL

val search_title_scenario = Scenario(title = "Search title displays") {
    When("I visit a search engine") {
        HttpGetAction(
            target = "search_engine",
            uri = "/",
            validations = mapOf("request accepted" to "status == 200".spEL()),
            outputs = mapOf("resultJson" to "body".spEL())
        )
    }
    Then("The search engine title is displayed") {
        AssertAction(
            listOf(
                "resultJson.contains('<title>Google</title>')".spEL()
            )
        )
    }
}

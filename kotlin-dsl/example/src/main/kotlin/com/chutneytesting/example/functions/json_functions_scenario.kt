package com.chutneytesting.example.functions

import com.chutneytesting.kotlin.dsl.*

val json_merge_scenario = Scenario(title = "Json merge") {
    Given("I have 2 JSON, A and B") {
        ContextPutAction(
            entries = mapOf(
                "jsonA" to "{\"toto\":\"tutu\"}",
                "jsonB" to "{\"titi\":\"tata\"}"
            )
        )
    }

    When ("I merge the JSON A and B to get JSON C") {
        ContextPutAction(
            entries = mapOf(
                "jsonC" to jsonMerge("jsonA".spELVar(), "jsonB".spELVar())
            )
        )
    }

    Then ("I check that JSON C is equal to JSON A + B") {
        JsonCompareAction("jsonC".spEL, "{\"toto\":\"tutu\",\"titi\":\"tata\"}")
    }
}

val json_set_scenario = Scenario(title = "Json set") {
    Given("I have one JSON, a path and a value") {
        ContextPutAction(
            entries = mapOf(
                "json" to "{\"toto\":\"tutu\"}",
                "path" to "$.toto",
                "value" to "titi"
            )
        )
    }

    When ("I set the value to the JSON with the path to get jsonSet") {
        ContextPutAction(
            entries = mapOf(
                "jsonSet" to jsonSet("json".spELVar(), "path".spELVar(), "value".spELVar())
            )
        )
    }

    Then ("I check that the value has been added to the json") {
        JsonCompareAction("jsonSet".spEL, "{\"toto\": \"titi\"}")
    }
}

val json_set_many_scenario = Scenario(title = "Json set many") {
    Given("I have one JSON, a map<path, value>") {
        ContextPutAction(
            entries = mapOf(
                "json" to "{\"toto\":\"tutu\", \"tyty\":\"tata\"}",
                "map" to mapOf(Pair("$.toto", "titi"), Pair("tyty", "toto"))
            )
        )
    }

    When ("I set the value to the JSON with the path to get jsonSetMany") {
        ContextPutAction(
            entries = mapOf(
                "jsonSetMany" to jsonSetMany("json".spELVar(), "map".spELVar())
            )
        )
    }

    Then ("I check that the value has been added to the json") {
        JsonCompareAction("jsonSetMany".spEL, "{\"toto\":\"titi\",\"tyty\":\"toto\"}")
    }
}

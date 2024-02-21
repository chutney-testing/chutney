package com.chutneytesting.kotlin.dsl

import com.chutneytesting.kotlin.asResourceContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert

class ChutneyScenarioDslTest {

    @Test
    fun `is able to create chutney scenario using kotlin dsl`() {

        val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            Given("I set get people service api endpoint") {
                ContextPutAction(entries = mapOf("uri" to "api/people/1"))
            }
            When("I send GET HTTP request", RetryTimeOutStrategy("5 s", "1 s")) {
                HttpGetAction(
                    target = "swapi.dev",
                    uri = "uri".spEL(),
                    validations = mapOf("always true" to "true".elEval())
                )
            }
            Then("I receive valid HTTP response") {
                JsonAssertAction(document = "body".spEL(), expected = mapOf("$.name" to "Luke Skywalker"))
            }
        }

        JSONAssert.assertEquals(
            "dsl/get-people.chutney.json".asResourceContent(),
            "$`swapi GET people record`",
            true
        )
    }

    @Test
    fun `is able to create chutney scenario with substeps using kotlin dsl`() {

        val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            Given("I set get people service api endpoint") {
                Step("set id") {
                    ContextPutAction(entries = mapOf("id" to "1"))
                }
                Step("set uri") {
                    ContextPutAction(entries = mapOf("uri" to "api/people/${"id".spEL()}"))
                }
            }
            When("I send GET HTTP request") {
                HttpGetAction(target = "swapi.dev", uri = "uri".spEL())
            }
            Then("I receive valid HTTP response") {
                JsonAssertAction(document = "body".spEL(), expected = mapOf("$.name" to "Luke Skywalker"))
            }
        }

        JSONAssert.assertEquals(
            "dsl/get-people-with-substeps.chutney.json".asResourceContent(),
            "$`swapi GET people record`",
            true
        )

    }

    @Test
    fun `is able to create chutney scenario using kotlin dsl with functions`() {

        fun declareUri(): ChutneyStepBuilder.() -> Unit = { ContextPutAction(entries = mapOf("uri" to "api/people/1")) }

        val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            Given("I set get people service api endpoint", declareUri())
            When("I send GET HTTP request", RetryTimeOutStrategy("5 s", "1 s")) {
                HttpGetAction(
                    target = "swapi.dev",
                    uri = "uri".spEL(),
                    validations = mapOf("always true" to "true".elEval())
                )
            }
            Then("I receive valid HTTP response") {
                JsonAssertAction(document = "body".spEL(), expected = mapOf("$.name" to "Luke Skywalker"))
            }
        }

        JSONAssert.assertEquals(
            "dsl/get-people.chutney.json".asResourceContent(),
            "$`swapi GET people record`",
            true
        )
    }

    @Test
    fun `is able to create chutney scenario using kotlin dsl with functions and multiple assertions`() {

        fun declareUri(): ChutneyStepBuilder.() -> Unit = { ContextPutAction(entries = mapOf("uri" to "api/people/1")) }

        val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            Given("I set get people service api endpoint", declareUri())
            When("I send GET HTTP request") {
                HttpGetAction(target = "swapi.dev", uri = "uri".spEL())
            }
            Then("I receive valid HTTP response") {
                JsonAssertAction(
                    document = "body".spEL(),
                    expected = mapOf("$.name" to "Luke Skywalker", "$.species" to emptyArray<String>())
                )
            }
        }

        JSONAssert.assertEquals(
            "dsl/get-people-multiple-assertions.chutney.json".asResourceContent(),
            "$`swapi GET people record`",
            true
        )
    }

    @Test
    fun `is able to create chutney scenario using kotlin dsl with extension functions`() {

        fun ChutneyStepBuilder.declareUri() = ContextPutAction(entries = mapOf("uri" to "api/people/1"))

        val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            Given("I set get people service api endpoint") {
                declareUri()
            }
            When("I send GET HTTP request") {
                HttpGetAction(
                    target = "swapi.dev",
                    uri = "uri".spEL(),
                    validations = mapOf("always true" to "true".elEval()),
                    strategy = RetryTimeOutStrategy("5 s", "1 s")
                )
            }
            Then("I receive valid HTTP response") {
                JsonAssertAction(document = "body".spEL(), expected = mapOf("$.name" to "Luke Skywalker"))
            }
        }

        JSONAssert.assertEquals(
            "dsl/get-people.chutney.json".asResourceContent(),
            "$`swapi GET people record`",
            true
        )
    }

    @Test
    fun `is able to create chutney scenario using kotlin dsl with softAssertions`() {

        fun declareUri(): ChutneyStepBuilder.() -> Unit = { ContextPutAction(entries = mapOf("uri" to "api/people/1")) }

        val `swapi GET people record` = Scenario(title = "swapi GET people record") {
            Given("I set get people service api endpoint", declareUri())
            When("I send GET HTTP request") {
                HttpGetAction(target = "swapi.dev", uri = "uri".spEL())
            }
            Then("I receive valid HTTP response", strategy = SoftAssertStrategy()) {
                JsonAssertAction(
                    document = "body".spEL(),
                    expected = mapOf("$.name" to "Luke Skywalker", "$.species" to emptyArray<String>())
                )
            }
        }

        JSONAssert.assertEquals(
            "dsl/get-people-multiple-assertions-soft-strategy.chutney.json".asResourceContent(),
            "$`swapi GET people record`",
            true
        )
    }

    @Test
    fun `should not have null or empty object in json final inputs`() {

        val chutneyScenario = Scenario(title = "No NULL in final") {
            When("final") {
                FinalAction("success", "success")
            }
        }

        val json = "$chutneyScenario"
        assertThat(json)
            .doesNotContain("null", "{}");
    }

    @Test
    fun `should generate json scenario with kafka Actions`() {

        val chutneyScenario = Scenario(title = "Kafka actions") {
            When("nothing") {  }
            Then("Publish") {
                KafkaBasicPublishAction(
                    target = "target", topic = "topic", payload = "payload",
                    properties = mapOf("bootstrap.servers" to "a.host:666,b.host:999")
                )
            }
            And("Consume") {
                KafkaBasicConsumeAction(
                    target = "target", topic = "topic", group = "group",
                    nbMessages = 2,
                    headerSelector = "$[json/path]",
                    contentType = "application/json",
                    ackMode = KafkaSpringOffsetCommitBehavior.MANUAL
                )
            }
        }

        JSONAssert.assertEquals(
            "dsl/kafka-actions.chutney.json".asResourceContent(),
            "$chutneyScenario",
            true
        )
    }
}


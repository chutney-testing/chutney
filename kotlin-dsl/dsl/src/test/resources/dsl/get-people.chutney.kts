import com.chutneytesting.kotlin.dsl.ContextPutAction
import com.chutneytesting.kotlin.dsl.HttpGetAction
import com.chutneytesting.kotlin.dsl.JsonAssertAction
import com.chutneytesting.kotlin.dsl.RetryTimeOutStrategy
import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.elEval

Scenario(title = "swapi GET people record") {
    Given("I set get people service api endpoint") {
        ContextPutAction(entries = mapOf("uri" to "api/people/1"))
    }
    When("I send GET HTTP request", RetryTimeOutStrategy("5 s", "1 s")) {
        HttpGetAction(target = "swapi.dev", uri = "\${#uri}", validations = mapOf("always true" to "true".elEval()))
    }
    Then("I receive valid HTTP response") {
        JsonAssertAction(document = "\${#body}", expected = mapOf("$.name" to "Luke Skywalker"))
    }
}

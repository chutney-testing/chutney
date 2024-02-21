!!! info "[Browse implementation](https://github.com/chutney-testing/chutney/blob/master/action-impl/src/main/java/com/chutneytesting/action/groovy/GroovyAction.java){:target="_blank"}"

This action executes a [Groovy](https://groovy-lang.org/documentation.html){:target="_blank"} script.

=== "Inputs"

    | Required | Name            | Type                     |     Default     | Description                                                                                                                                                                                                      |
    |:--------:|:----------------|:-------------------------|:----------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
    |    *     | `script`        | String                   |                 | The groovy script to be executed. The last statement must return a Map either implicitly or explicitly. </br> For a script that just launch background execution, an empty map ([:]) must be the last statement. |
    |          | `parameters`    | Map <String, Object>     |                 | Key/values parameters to be used in the script                                                                                                                                                                   |

=== "Outputs"

    | Name            | Type                           | Description                                                    |
    |:----------------|:-------------------------------|:---------------------------------------------------------------|
    | `status`        | Status enum (Success, Failure) | Execution status of the groovy task                            |
    | `outputs`       | Map <String, Object>           | The map returned by the last statement of the executed script. |

Example:
=== "Kotlin"
    ``` kotlin

    import com.chutneytesting.kotlin.dsl.AssertAction
    import com.chutneytesting.kotlin.dsl.GroovyAction
    import com.chutneytesting.kotlin.dsl.Scenario

    val my_groovy_scenario = Scenario(title = "my groovy scenario") {
        When("I run my script") {
            GroovyAction(
                script = """
                    int sum = left + right
                    return ['computation': sum]
                        """.trimIndent(),
                parameters = mapOf(
                    "left" to 1,
                    "right" to 2
                )
            )
        }
        Then("I check computation") {
            AssertAction(
                asserts = listOf(
                    "\${#computation == 3}"
                )
            )
        }
    }
    ```

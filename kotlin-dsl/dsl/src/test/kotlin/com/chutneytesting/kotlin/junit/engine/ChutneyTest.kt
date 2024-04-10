package com.chutneytesting.kotlin.junit.engine

import com.chutneytesting.kotlin.dsl.*
import com.chutneytesting.kotlin.junit.api.ChutneyTest

class ChutneyTest {

    @ChutneyTest
    fun sameMethodNameInOtherClassTest(): ChutneyScenario {
        return Scenario(title = "A scenario") {
            When("Something happens") {
                SuccessAction()
            }
        }
    }

    @ChutneyTest
    fun withFinalAction(): ChutneyScenario {
        return Scenario(title = "A final action scenario") {
            When("Final action is registered") {
                FinalAction("final action name", "success")
            }
        }
    }

    @ChutneyTest
    fun withRetryStrategyFinalAction(): ChutneyScenario {
        return Scenario(title = "A retry strategy final action scenario") {
            Given("A date to reach") {
                ContextPutAction(
                    mapOf("dateToPass" to "now().plusSeconds(2)".spEL())
                )
            }
            When("Final action is registered to wait for time to pass") {
                FinalAction("Assert time passes...", "success",
                    strategyType = RetryTimeOutStrategy.TYPE,
                    strategyProperties = mapOf("timeOut" to "3 s", "retryDelay" to "1 s"),
                    validations = mapOf("date is past" to "now().isAfter(#dateToPass)".spEL())
                )
            }
        }
    }

    @ChutneyTest
    fun withRetryStrategy(): ChutneyScenario {
        return Scenario(title = "A scenario with retry strategy") {
            Given("A number to reach") {
                ContextPutAction(
                    mapOf("index" to "\${0}", "numToReach" to "\${3}")
                )
            }
            When("Validation is triggered multiple times", RetryTimeOutStrategy("1 s", "100 ms")) {
                Step("Update index") {
                    ContextPutAction(
                        mapOf("index" to "index + 1".spEL())
                    )
                }
                Step("Assert num is reach") {
                    AssertAction(
                        listOf("index == #numToReach".spEL())
                    )
                }
            }
        }
    }

    @ChutneyTest
    fun withInnerRetryStrategy(): ChutneyScenario {
        return Scenario(title = "A scenario with inner retry strategy") {
            Given("A number to reach") {
                ContextPutAction(
                    mapOf("index" to "\${0}", "numToReach" to "\${3}")
                )
            }
            When("Validation is triggered multiple times", RetryTimeOutStrategy("2 s", "100 ms")) {
                Step("Update index") {
                    ContextPutAction(
                        mapOf("index" to "index + 1".spEL())
                    )
                }
                Step("Another number to reach") {
                    ContextPutAction(
                        mapOf("anotherIndex" to "\${0}", "anotherNumToReach" to "\${2}")
                    )
                }
                Step("Another inner validation triggered multiple times", RetryTimeOutStrategy("1 s", "100 ms")) {
                    Step("Update another index") {
                        ContextPutAction(
                            mapOf("anotherIndex" to "anotherIndex + 1".spEL())
                        )
                    }
                    Step("Assert another num is reach") {
                        AssertAction(
                            listOf("anotherIndex == #anotherNumToReach".spEL())
                        )
                    }
                }
                Step("Assert num is reach") {
                    AssertAction(
                        listOf("index == #numToReach".spEL())
                    )
                }
            }
        }
    }

    @ChutneyTest
    fun withSubSteps(): ChutneyScenario {
        return Scenario(title = "A scenario") {
            Given("An initial state") {
                Step("A sub step for setting the state") {
                    SuccessAction()
                }
                Step("Another sub step for setting the state") {
                    SuccessAction()
                }
            }
            When("Action is triggered") {
                Step("A sub step for action") {
                    SuccessAction()
                }
                Step("Another sub step for action") {
                    SuccessAction()
                }
            }
            Then("A new state is there") {
                Step("A sub step for validating the new state") {
                    SuccessAction()
                }
                Step("Another sub step for validating the new state") {
                    SuccessAction()
                }
            }
        }
    }

    @ChutneyTest
    fun withScenarioList(): List<ChutneyScenario> {
        return listOf(
            Scenario(title = "First scenario") {
                When("Action is triggered") {
                    SuccessAction()
                }
            },
            Scenario(title = "Second scenario") {
                When("Action is triggered") {
                    SuccessAction()
                }
            }
        )
    }

    @ChutneyTest
    fun withIfStrategy(): ChutneyScenario {
        return Scenario(title = "A scenario") {
            Given("Two values") {
                ContextPutAction(
                    entries = mapOf(
                        "value1" to "",
                        "value2" to ""
                    )
                )
            }
            When("Updating values or not depending of strategy") {
                Step("Put will be done", IfStrategy(condition = "(1+1) == 2".elEval())) {
                    ContextPutAction(
                        entries = mapOf("value1" to "should_be_there")
                    )
                }
                Step("Put will not be done", IfStrategy(condition = "(1+1) == 3".elEval())) {
                    ContextPutAction(
                        entries = mapOf("value2" to "should_not_be_there")
                    )
                }
            }
            Then("value1 should be updated and value2 should not be updated") {
                AssertAction(
                    asserts = listOf(
                        "value1.equals(\"should_be_there\")".spEL,
                        "value2.equals(\"\")".spEL
                    )
                )
            }
        }
    }
}

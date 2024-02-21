package com.chutneytesting.idea.actions.converter.v1

import java.util.stream.Collectors

class ScenarioV1 @JvmOverloads constructor(private val content: MutableMap<String, Any?> = mutableMapOf()) {
    fun name(): String {
        return content["name"] as String
    }

    fun name(n: String?): ScenarioV1 {
        content["name"] = n
        return this
    }

    fun steps(steps: List<StepV1>): ScenarioV1 {
        content["steps"] = steps.stream().map { it: StepV1 -> it.asMap() }.collect(Collectors.toList())
        return this
    }

    fun rootStep(): ScenarioV1 {
        content["gwtType"] = "ROOT_STEP"
        return this
    }

    fun gwtSteps(): GWTSteps {
        return GWTStepReader().read(content)
    }

    fun asMap(): Map<String, Any?> {
        return content
    }

}

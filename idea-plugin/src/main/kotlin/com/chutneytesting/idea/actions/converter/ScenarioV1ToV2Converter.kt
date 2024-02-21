package com.chutneytesting.idea.actions.converter

import com.chutneytesting.idea.actions.converter.v1.ScenarioV1
import com.chutneytesting.idea.actions.converter.v1.StepV1
import com.chutneytesting.idea.actions.converter.v2.ScenarioV2
import java.util.stream.Collectors

class ScenarioV1ToV2Converter {
    private val mapper = JsonSerializer()
    private val stepConverter = StepV1ToV2Converter()
    fun convert(rawScenarioV1: String?): String {
        val scenarioV2 = getScenarioV2(rawScenarioV1)
        val map = scenarioV2.asMap()
        return mapper.toString(map)
    }

    fun getScenarioV2(rawScenarioV1String: String?): ScenarioV2 {
        val rawScenario = mapper.toMap(rawScenarioV1String)["scenario"] as MutableMap<String, Any?>
        val scenarioV1 = ScenarioV1(rawScenario)
        val gwtSteps = scenarioV1.gwtSteps()
        return ScenarioV2()
            .title(scenarioV1.name())
            .givens(gwtSteps.givens.stream().map { stepV1: StepV1? -> stepConverter.convert(stepV1) }.collect(Collectors.toList()))
            .`when`(stepConverter.convert(gwtSteps.`when`))
            .thens(gwtSteps.thens.stream().map { stepV1: StepV1? -> stepConverter.convert(stepV1) }.collect(Collectors.toList()))
    }
}

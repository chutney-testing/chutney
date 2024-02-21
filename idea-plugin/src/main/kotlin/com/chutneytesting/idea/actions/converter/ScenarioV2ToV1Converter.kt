package com.chutneytesting.idea.actions.converter

import com.chutneytesting.idea.actions.converter.v1.ScenarioV1
import com.chutneytesting.idea.actions.converter.v1.StepV1
import com.chutneytesting.idea.actions.converter.v2.ScenarioV2
import com.google.common.collect.Maps
import java.util.*

class ScenarioV2ToV1Converter {
    private val mapper = JsonSerializer()
    private val stepConverter = StepV2ToV1Converter()
    fun convert(rawScenarioV2String: String?): String {
        val rawScenario = mapper.toMap(rawScenarioV2String)
        val scenarioV2 = ScenarioV2(rawScenario)
        val l = mutableListOf<StepV1>()
        scenarioV2.givens().stream().map { stepConverter.convertWithGWTType(it!!, "GIVEN") }
            .forEach { e: StepV1 -> l.add(e) }
        Optional.ofNullable(scenarioV2.`when`()).map { stepConverter.convertWithGWTType(it!!, "WHEN") }
            .ifPresent { e: StepV1 -> l.add(e) }
        scenarioV2.thens().stream().map { stepConverter.convertWithGWTType(it!!, "THEN") }
            .forEach { e: StepV1 -> l.add(e) }
        val scenarioV1 = ScenarioV1().rootStep().name(scenarioV2.title()).steps(l)
        val scenario: MutableMap<String?, Any?> = Maps.newHashMap()
        scenario["scenario"] = scenarioV1.asMap()
        return mapper.toString(scenario)
    }
}

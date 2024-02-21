package com.chutneytesting.idea.actions.converter.v1

import com.google.common.collect.Lists
import java.util.*

internal class GWTStepReader {
    fun read(rawScenarioV1: Map<String, Any?>): GWTSteps {
        val result = GWTSteps()
        var currentStepsList = result.givens
        var currentGWTType = "GIVEN"
        val allSteps = rawScenarioV1.getOrDefault("steps", Lists.newLinkedList<Any>()) as List<MutableMap<String, Any?>>
        for (rawStep in allSteps) {
            val step = StepV1(rawStep)
            val stepGWTType = Optional.ofNullable(step.gwtType()).orElse(currentGWTType)
            if ("THEN" == currentGWTType || currentGWTType == stepGWTType) {
                currentStepsList.add(step) // le type n'a pas change ou bloc THEN en cours
            } else if ("WHEN" == stepGWTType) {
                result.`when` = step // Remplir le WHEN
                currentGWTType = "THEN" // passer au bloc THEN
                currentStepsList = result.thens
            } else {
                currentGWTType = "THEN" //
                currentStepsList = result.thens
                currentStepsList.add(step)
            }
        }
        return result
    }
}

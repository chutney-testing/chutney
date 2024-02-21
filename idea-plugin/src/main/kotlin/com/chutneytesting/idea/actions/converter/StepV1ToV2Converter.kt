package com.chutneytesting.idea.actions.converter

import com.chutneytesting.idea.actions.converter.v1.StepV1
import com.chutneytesting.idea.actions.converter.v2.StepV2

internal class StepV1ToV2Converter {
    fun convert(stepV1: StepV1?): StepV2? {
        return if (stepV1 == null) {
            null
        } else if (stepV1.isTaskStep()) {
            StepV2().description(stepV1.name()).strategy(stepV1.strategy()).implementation(stepV1.asCleanedMap())
                .xRef(stepV1.xRef())
        } else {
            val subSteps: List<StepV2> = stepV1.steps().mapNotNull { stepV1: StepV1? -> convert(stepV1) }
            StepV2().description(stepV1.name()).subSteps(subSteps).xRef(stepV1.xRef())
        }
    }
}

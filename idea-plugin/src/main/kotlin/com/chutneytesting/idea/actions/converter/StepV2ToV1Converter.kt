package com.chutneytesting.idea.actions.converter

import com.chutneytesting.idea.actions.converter.v1.StepV1
import com.chutneytesting.idea.actions.converter.v2.StepV2
import java.util.stream.Collectors

internal class StepV2ToV1Converter {
    fun convertWithGWTType(stepV2: StepV2, gwtType: String?): StepV1 {
        return if (stepV2.isTaskStep) {
            if (stepV2.isRef) {
                StepV1().ref(stepV2.xRef())
            } else {
                StepV1().gwtType(gwtType).name(stepV2.description()).strategy(stepV2.strategy())
                    .concat(stepV2.implementation())
            }
        } else {
            if (stepV2.isRef) {
                return StepV1().ref(stepV2.xRef())
            }
            val subSteps = stepV2.subSteps().stream().map { it: StepV2 -> convertWithGWTType(it, null) }
                .collect(Collectors.toList())
            StepV1().gwtType(gwtType).name(stepV2.description()).steps(subSteps)
        }
    }
}

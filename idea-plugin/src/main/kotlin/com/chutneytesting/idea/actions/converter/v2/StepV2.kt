package com.chutneytesting.idea.actions.converter.v2

import java.util.stream.Collectors

class StepV2 @JvmOverloads constructor(private val content: MutableMap<String, Any?> = mutableMapOf()) {
    val isTaskStep: Boolean
        get() = content.containsKey("implementation")

    /* public boolean isTaskStepWithRef() {
        return content.containsKey("implementation") && ((Map<String, Object>) content.get("implementation")).containsKey("x-$ref");
    }*/
    val isRef: Boolean
        get() = content.containsKey("x-\$ref")

    fun description(): String? {
        return content["description"] as String?
    }

    fun xRef(): String? {
        return content["x-\$ref"] as String?
    }

    fun description(d: String?): StepV2 {
        content["description"] = d
        return this
    }

    fun subSteps(l: List<StepV2>): StepV2 {
        content["subSteps"] =
            l.stream().map<Map<String, Any?>> { it: StepV2 -> it.content }.collect(Collectors.toList())
        return this
    }

    fun subSteps(): List<StepV2> {
        val l = content["subSteps"] as List<MutableMap<String, Any?>>?
        return if (l != null) l.stream().map { content: MutableMap<String, Any?> -> StepV2(content) }.collect(Collectors.toList()) else emptyList()
    }

    fun implementation(m: Map<String, Any?>?): StepV2 {
        content["implementation"] = m
        return this
    }

    fun implementation(): Map<String, Any?> {
        return content["implementation"] as Map<String, Any?>
    }

    fun strategy(s: Map<String, Any?>?): StepV2 {
        content["strategy"] = s
        return this
    }

    fun xRef(d: String?): StepV2 {
        content["x-\$ref"] = d
        return this
    }

    fun strategy(): Map<String, Any?>? {
        return content["strategy"] as Map<String, Any?>?
    }

    fun asMap(): Map<String, Any?> {
        return content
    }

}

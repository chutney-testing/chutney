package com.chutneytesting.idea.actions.converter.v1

import com.google.common.collect.Maps
import java.util.stream.Collectors

class StepV1 @JvmOverloads constructor(private val content: MutableMap<String, Any?> = mutableMapOf()) {
    fun gwtType(): String {
        return content["gwtType"] as String
    }

    fun gwtType(g: String?): StepV1 {
        content["gwtType"] = g
        return this
    }

    fun isTaskStep(): Boolean {
        return content.containsKey("type")
    }

    fun name(): String? {
        return content["name"] as String?
    }

    fun name(n: String?): StepV1 {
        content["name"] = n
        return this
    }

    fun strategy(): Map<String, Any?>? {
        return content["strategy"] as Map<String, Any?>?
    }

    fun strategy(m: Map<String, Any?>?): StepV1 {
        content["strategy"] = m
        return this
    }

    fun concat(m: Map<String, Any?>): StepV1 {
        content.putAll(m)
        return this
    }

    fun steps(): List<StepV1> {
        return (content.getOrDefault("steps", emptyList<Any>()) as List<MutableMap<String, Any?>>).stream()
            .map { content: MutableMap<String, Any?> -> StepV1(content) }
            .collect(Collectors.toList())
    }

    fun steps(l: List<StepV1>): StepV1 {
        content["steps"] = l.stream().map<Map<String, Any?>> { it: StepV1 -> it.content }.collect(Collectors.toList())
        return this
    }

    fun asCleanedMap(): Map<String, Any?> {
        val ret: MutableMap<String, Any?> = Maps.newLinkedHashMap(content)
        ret.remove("name")
        ret.remove("gwtType")
        ret.remove("strategy")
        ret.remove("x-\$ref")
        ret.remove("\$ref")
        return ret
    }

    fun asMap(): Map<String, Any?> {
        return content
    }

    fun ref(ref: String?): StepV1 {
        content["\$ref"] = ref
        return this
    }

    fun xRef(): String? {
        return content["x-\$ref"] as String?
    }

}


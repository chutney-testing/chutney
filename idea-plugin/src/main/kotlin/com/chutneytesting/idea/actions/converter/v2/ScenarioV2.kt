package com.chutneytesting.idea.actions.converter.v2

import java.util.*
import java.util.stream.Collectors

class ScenarioV2 @JvmOverloads constructor(private val content: MutableMap<String, Any?> = mutableMapOf()) {
    fun title(t: String?): ScenarioV2 {
        content["title"] = t
        return this
    }

    fun title(): String {
        return content["title"] as String
    }

    fun description(d: String?): ScenarioV2 {
        content["description"] = d
        return this
    }

    fun description(): String {
        return content["description"] as String
    }

    fun givens(l: MutableList<StepV2>): ScenarioV2 {
        content["givens"] = l.stream().map { obj: StepV2 -> obj.asMap() }.collect(Collectors.toList())
        return this
    }

    fun givens(): List<StepV2> {
        val l = content["givens"] as List<MutableMap<String, Any?>>? ?: return emptyList()
        return l.stream().map { content: MutableMap<String, Any?> -> StepV2(content) }.collect(Collectors.toList())
    }

    fun `when`(s: StepV2?): ScenarioV2 {
        content["when"] = s?.asMap()
        return this
    }

    fun `when`(): StepV2 {
        return Optional.ofNullable(content["when"] as MutableMap<String, Any?>)
            .map { content: MutableMap<String, Any?> -> StepV2(content) }.orElse(null)
    }

    fun thens(l: MutableList<StepV2>): ScenarioV2 {
        content["thens"] = l.stream().map { obj: StepV2 -> obj.asMap() }.collect(Collectors.toList())
        return this
    }

    fun thens(): List<StepV2> {
        val l = content["thens"] as List<MutableMap<String, Any?>>? ?: return emptyList()
        return l.stream().map { content: MutableMap<String, Any?> -> StepV2(content) }.collect(Collectors.toList())
    }

    fun asMap(): Map<String, Any?> {
        return content
    }

}

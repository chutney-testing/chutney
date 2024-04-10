package com.chutneytesting.kotlin.dsl

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.core.util.Separators
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

@DslMarker
annotation class ChutneyScenarioDsl

fun Scenario(
    id: Int? = null,
    title: String,
    defaultDataset: String? = null,
    tags: List<String> = emptyList(),
    block: ChutneyScenarioBuilder.() -> Unit
): ChutneyScenario {
    return ChutneyScenarioBuilder(id, title, defaultDataset, tags).apply(block).build()
}

@ChutneyScenarioDsl
class ChutneyScenarioBuilder(
    val id: Int? = null,
    val title: String = "",
    val defaultDataset: String? = null,
    val tags: List<String> = emptyList(),
) {
    var description = title
    private val givens = mutableListOf<ChutneyStep>()
    private var `when`: ChutneyStep? = null
    private val thens = mutableListOf<ChutneyStep>()

    fun Given(description: String = "", block: ChutneyStepBuilder.() -> Unit) {
        givens.add(ChutneyStepBuilder(description).apply(block).build())
    }

    fun Given(description: String = "", strategy: Strategy? = null, block: ChutneyStepBuilder.() -> Unit) {
        givens.add(ChutneyStepBuilder(description, strategy).apply(block).build())
    }

    fun When(description: String = "", block: ChutneyStepBuilder.() -> Unit) {
        `when` = ChutneyStepBuilder(description).apply(block).build()
    }

    fun When(description: String = "", strategy: Strategy? = null, block: ChutneyStepBuilder.() -> Unit) {
        `when` = ChutneyStepBuilder(description, strategy).apply(block).build()
    }

    fun Then(description: String = "", block: ChutneyStepBuilder.() -> Unit) {
        thens.add(ChutneyStepBuilder(description).apply(block).build())
    }

    fun Then(description: String = "", strategy: Strategy?, block: ChutneyStepBuilder.() -> Unit) {
        thens.add(ChutneyStepBuilder(description, strategy).apply(block).build())
    }

    fun And(description: String = "", strategy: Strategy? = null, block: ChutneyStepBuilder.() -> Unit) {
        when {
            `when` != null -> thens.add(ChutneyStepBuilder(description, strategy).apply(block).build())
            else -> givens.add(ChutneyStepBuilder(description, strategy).apply(block).build())
        }
    }

    fun And(description: String = "", block: ChutneyStepBuilder.() -> Unit) {
        when {
            `when` != null -> thens.add(ChutneyStepBuilder(description).apply(block).build())
            else -> givens.add(ChutneyStepBuilder(description).apply(block).build())
        }
    }

    fun build(): ChutneyScenario = ChutneyScenario(id, title, description, defaultDataset, tags, givens, `when`, thens)
}

@JsonInclude(NON_EMPTY)
open class Strategy(val type: String, val parameters: Map<String, String> = emptyMap())
open class RetryTimeOutStrategy(timeout: String, retryDelay: String) :
    Strategy(type = TYPE, parameters = mapOf("timeOut" to timeout, "retryDelay" to retryDelay)) {
    companion object {
        const val TYPE: String = "retry-with-timeout"
    }
}

open class ForStrategy(dataset: String = "dataset".spEL, index: String = "i") :
    Strategy(type = "for", parameters = mapOf("dataset" to dataset, "index" to index)) {
}
open class IfStrategy(condition: String) :
    Strategy(type = "if", parameters = mapOf("condition" to condition)) {
}

open class SoftAssertStrategy :
    Strategy(type = "soft-assert")

@ChutneyScenarioDsl
class ChutneyStepBuilder(var description: String = "", var strategy: Strategy? = null) {

    var subSteps = mutableListOf<ChutneyStep>()
    var implementation: ChutneyStepImpl? = null

    fun Strategy(s: Strategy) {
        strategy = s
    }

    fun Implementation(block: ChutneyStepImplBuilder.() -> Unit) {
        implementation = ChutneyStepImplBuilder().apply(block).build()
    }

    fun Step(description: String = "", strategy: Strategy? = null, block: ChutneyStepBuilder.() -> Unit) {
        subSteps.add(ChutneyStepBuilder(description, strategy).apply(block).build())
    }

    fun Step(description: String = "", block: ChutneyStepBuilder.() -> Unit) {
        subSteps.add(ChutneyStepBuilder(description).apply(block).build())
    }

    fun build(): ChutneyStep = ChutneyStep(description, implementation, strategy, subSteps)
}

@ChutneyScenarioDsl
class ChutneyStepImplBuilder {

    var type: String = ""
    var target: String? = null
    var inputs: Map<String, Any> = mapOf()
    var outputs: Map<String, Any> = mapOf()
    var validations: Map<String, Any> = mapOf()

    fun build(): ChutneyStepImpl = ChutneyStepImpl(type, target, inputs, outputs, validations)

}

object Mapper {

    private val pp = object : DefaultPrettyPrinter() {
        init {
            val indenter: Indenter = DefaultIndenter()
            indentObjectsWith(indenter) // Indent JSON objects
            indentArraysWith(indenter) // Indent JSON arrays
        }

        override fun createInstance(): DefaultPrettyPrinter {
            return DefaultPrettyPrinter(this);
        }

        override fun withSeparators(separators: Separators?): DefaultPrettyPrinter {
            _separators = separators
            _objectFieldValueSeparatorWithSpaces = "" + separators!!.objectFieldValueSeparator + " "
            return this
        }
    }

    private val mapper: ObjectMapper = jacksonObjectMapper()
        .setDefaultPrettyPrinter(pp)
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        .enable(SerializationFeature.INDENT_OUTPUT)

    fun toJson(value: Any): String {
        var json = mapper.writeValueAsString(value)
        json = json.replace(": [ ]", ": []")
        json = json.replace(": { }", ": {}")
        json += System.lineSeparator()
        return json
    }
}

class ChutneyStep(
    val description: String,
    @JsonInclude(NON_NULL) val implementation: ChutneyStepImpl? = null,
    @JsonInclude(NON_NULL) val strategy: Strategy? = null,
    @JsonInclude(NON_EMPTY) val subSteps: List<ChutneyStep> = emptyList()
)

class ChutneyStepImpl(
    val type: String,
    @JsonInclude(NON_EMPTY) val target: String? = null,
    @JsonInclude(NON_EMPTY) val inputs: Map<String, Any> = mapOf(),
    @JsonInclude(NON_EMPTY) val outputs: Map<String, Any> = mapOf(),
    @JsonInclude(NON_EMPTY) val validations: Map<String, Any> = mapOf()
)

class ChutneyScenario(
    @JsonIgnore val id: Int?,
    val title: String = "",
    val description: String = "",
    @JsonIgnore val defaultDataset: String? = null,
    @JsonIgnore val tags: List<String> = emptyList(),
    @JsonInclude(NON_EMPTY) val givens: List<ChutneyStep> = mutableListOf(),
    @JsonInclude(NON_NULL) val `when`: ChutneyStep? = null,
    @JsonInclude(NON_EMPTY) val thens: List<ChutneyStep> = mutableListOf()
) {

    override fun toString(): String {
        return try {
            Mapper.toJson(this)
        } catch (e: Throwable) {
            title
        }
    }
}

package com.chutneytesting.idea.actions.converter

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.readValue
import org.hjson.JsonValue
import java.io.IOException

class JsonSerializer {
    private val mapper = ObjectMapper() //.findAndRegisterModules()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .enable(SerializationFeature.INDENT_OUTPUT)
        .setSerializationInclusion(JsonInclude.Include.NON_NULL)

    fun toMap(rawScenarioV1: String?): MutableMap<String, Any?> {
        return try {
            val json = JsonValue.readHjson(rawScenarioV1).toString()
            mapper.readValue(json)
        } catch (e: IOException) {
            throw IllegalStateException(e)
        }
    }

    fun toString(map: Map<*, *>?): String {
        return try {
            mapper.writeValueAsString(map)
        } catch (e: JsonProcessingException) {
            throw IllegalStateException(e)
        }
    }
}

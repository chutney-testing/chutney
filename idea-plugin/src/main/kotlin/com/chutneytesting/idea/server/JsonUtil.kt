package com.chutneytesting.idea.server

import com.google.gson.JsonElement
import com.google.gson.JsonObject

object JsonUtil {

    @Deprecated("")
    fun getString(`object`: JsonObject, key: String): String? {
        return if (`object` == null) {
            null
        } else key.let { getChildAsString(`object`, it) }
    }

    @JvmStatic
    fun getChildAsString(`object`: JsonObject?, key: String): String? {
        if (key == null) {
            return null
        }
        val var2 = `object`?.get(key)
        return getString(var2)
    }

    @JvmStatic
    fun getChildAsObject(`object`: JsonObject?, memberName: String): JsonObject? {
        return if (memberName == null) {
            null
        } else getAsObject(`object`?.get(memberName))
    }

    fun getAsObject(value: JsonElement?): JsonObject? {
        return if (value != null && value.isJsonObject) value.asJsonObject else null
    }

    fun getString(element: JsonElement?): String? {
        if (element != null && element.isJsonPrimitive) {
            val var1 = element.asJsonPrimitive
            if (var1.isString) {
                return var1.asString
            }
        }
        return null
    }
}

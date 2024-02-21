package com.chutneytesting.idea.completion

import com.google.gson.Gson
import com.intellij.openapi.application.PathManager
import com.intellij.util.PathUtil
import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader

object TargetsValueCompletionHelper {
    //e.printStackTrace();
    val targets: List<Target>
        get() {
            try {
                val chutneyDirectoryConf = PathUtil.toSystemIndependentName(PathManager.getConfigPath() + "/chutney-idea-plugin/conf/")
                val confFile = chutneyDirectoryConf + "environment/" + "GLOBAL.json"
                val fileReader = FileReader(File(confFile))
                val result = Gson().fromJson(fileReader, Result::class.java)
                return if (result?.targets == null) emptyList() else result.targets!!
            } catch (e: FileNotFoundException) { //e.printStackTrace();
            }
            return emptyList()
        }

    class Credential {
        var username: String? = null
        var password: String? = null
        private val additionalProperties: MutableMap<String, Any> = HashMap()

        fun getAdditionalProperties(): Map<String, Any> {
            return additionalProperties
        }

        fun setAdditionalProperty(name: String, value: Any) {
            additionalProperties[name] = value
        }
    }

    class Result {
        var name: String? = null
        var description: String? = null
        var targets: List<Target>? = null
        private val additionalProperties: MutableMap<String, Any> = HashMap()

        fun getAdditionalProperties(): Map<String, Any> {
            return additionalProperties
        }

        fun setAdditionalProperty(name: String, value: Any) {
            additionalProperties[name] = value
        }
    }

    class Security {
        var keyStore: String? = null
        var keyStorePassword: String? = null
        var credential: Credential? = null
        private val additionalProperties: MutableMap<String, Any> = HashMap()

        fun getAdditionalProperties(): Map<String, Any> {
            return additionalProperties
        }

        fun setAdditionalProperty(name: String, value: Any) {
            additionalProperties[name] = value
        }
    }

    class Target {
        var url: String? = null
        var security: Security? = null
        var name: String? = null
        private val additionalProperties: MutableMap<String, Any> = HashMap()

        fun getAdditionalProperties(): Map<String, Any> {
            return additionalProperties
        }

        fun setAdditionalProperty(name: String, value: Any) {
            additionalProperties[name] = value
        }
    }
}

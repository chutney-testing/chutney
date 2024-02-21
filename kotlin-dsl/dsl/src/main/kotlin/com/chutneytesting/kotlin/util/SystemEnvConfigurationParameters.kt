package com.chutneytesting.kotlin.util

import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
import java.util.*
import java.util.Optional.empty
import java.util.Optional.ofNullable

class SystemEnvConfigurationParameters()  {

    private val env = System.getenv()

    /**
     * LauncherDiscoveryRequestBuilder load properties from junit.properties and System.getProperties()
     */
    private val delegate = LauncherDiscoveryRequestBuilder.request().build().configurationParameters;

    fun get(key: String?): Optional<String> {
        val delegateValue = delegate?.get(key) ?: empty()
        return if (delegateValue.isEmpty) {
            ofNullable(env[key])
        } else {
            delegateValue
        }
    }

    fun getBoolean(key: String?): Optional<Boolean> {
        val delegateValue = delegate?.getBoolean(key) ?: empty()
        return if (delegateValue.isEmpty) {
            ofNullable(env[key]).map { it.toBoolean() }
        } else {
            delegateValue
        }
    }

}

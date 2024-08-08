package com.chutneytesting.kotlin.util

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junitpioneer.jupiter.SetSystemProperty
import java.util.*
import java.util.Optional.empty
import java.util.Optional.of

internal class SystemEnvConfigurationParametersTest {

    @Test
    @SetSystemProperty(key = "env_key", value = "env_value")
    fun getInSystemEnv() {
        val systemEnvConfParams = SystemEnvConfigurationParameters();

        val existingKey = systemEnvConfParams.get("env_key")
        assertEquals(existingKey,  of("env_value"))
    }

    @Test
    fun getInSystemProperties() {
        val systemEnvConfParams = SystemEnvConfigurationParameters();

        val notExistingKey = systemEnvConfParams.get("property_env_key")
        assertEquals(notExistingKey, empty<String>())

        System.setProperty("property_env_key", "env_value")
        val existingKey = systemEnvConfParams.get("property_env_key")
        assertEquals(existingKey,  of("env_value"))
    }

    @Test
    @SetSystemProperty(key = "bool_env_key", value = "true")
    fun getBooleanInSystemEnv() {
        val systemEnvConfParams = SystemEnvConfigurationParameters();

        val existingKey = systemEnvConfParams.getBoolean("bool_env_key")
        assertEquals(existingKey,  of(true))
    }

    @Test
    fun getBooleanInSystemProperties() {
        val systemEnvConfParams = SystemEnvConfigurationParameters();

        val notExistingKey = systemEnvConfParams.getBoolean("bool_property_env_key")
        assertEquals(notExistingKey, empty<Boolean>())

        System.setProperty("bool_property_env_key", "true")
        val existingKey = systemEnvConfParams.getBoolean("bool_property_env_key")
        assertEquals(existingKey,  of(true))
    }
}

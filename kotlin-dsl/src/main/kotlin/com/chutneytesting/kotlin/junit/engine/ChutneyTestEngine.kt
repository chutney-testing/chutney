package com.chutneytesting.kotlin.junit.engine

import com.chutneytesting.kotlin.ChutneyConfigurationParameters.CONFIG_ENGINE_STEP_AS_TEST
import com.chutneytesting.kotlin.junit.engine.execution.ChutneyEngineExecutionContext
import com.chutneytesting.kotlin.util.SystemEnvConfigurationParameters
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.UniqueId
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

class ChutneyTestEngine : TestEngine {

    companion object {
        const val CHUTNEY_JUNIT_ENGINE_ID = "chutney-kotlin-junit-engine"
        const val CHUTNEY_JUNIT_ENGINE_DISPLAY_NAME = "KChutney"
        val logger: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun getId(): String {
        return CHUTNEY_JUNIT_ENGINE_ID
    }

    override fun getGroupId(): Optional<String> {
        return Optional.of("com.chutneytesting")
    }

    override fun getArtifactId(): Optional<String> {
        return Optional.of("chutney-kotlin-dsl")
    }

    override fun discover(discoveryRequest: EngineDiscoveryRequest, uniqueId: UniqueId): TestDescriptor {
        try {
            val stepAsTest = SystemEnvConfigurationParameters().getBoolean(CONFIG_ENGINE_STEP_AS_TEST.parameter).orElse(true)
            val engineDescriptor = ChutneyEngineDescriptor(uniqueId, CHUTNEY_JUNIT_ENGINE_DISPLAY_NAME)
            DiscoverySelectorResolver(stepAsTest).resolveSelectors(discoveryRequest, engineDescriptor)
            return engineDescriptor
        } catch (e: Exception) {
            logger.error("{} discovery error", id, e)
            throw e
        }
    }

    override fun execute(request: ExecutionRequest) {
        ChutneyEngineExecutionContext(request).execute()
    }
}

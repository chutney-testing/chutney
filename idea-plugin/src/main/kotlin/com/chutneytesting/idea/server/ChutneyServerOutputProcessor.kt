package com.chutneytesting.idea.server

import com.chutneytesting.idea.common.ChutneyCommonConstants
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.intellij.execution.process.ProcessAdapter
import com.intellij.execution.process.ProcessEvent
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.process.ProcessOutputTypes
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.util.Key
import com.intellij.openapi.util.Pair
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

class ChutneyServerOutputProcessor(processHandler: ProcessHandler) {
    private val myTexts: Queue<Pair<String, Key<*>>> = LinkedList()
    private val myListeners: MutableList<ChutneyServerOutputListener> = CopyOnWriteArrayList()
    fun addListener(listener: ChutneyServerOutputListener) {
        myListeners.add(listener)
        for (text in myTexts) {
            listener.onOutputAvailable(text.getFirst(), text.getSecond())
        }
    }

    private fun handleLineAsEvent(line: String): Boolean {
        var line = line
        if (line.contains("Started ServerBootstrap")) {
            line =
                ChutneyCommonConstants.EVENT_PREFIX + "{\"type\":\"server_started\"}" + ChutneyCommonConstants.EVENT_SUFFIX
        }
        if (line.startsWith(ChutneyCommonConstants.EVENT_PREFIX) && line.endsWith(ChutneyCommonConstants.EVENT_SUFFIX)) {
            val json = line.substring(
                ChutneyCommonConstants.EVENT_PREFIX.length,
                line.length - ChutneyCommonConstants.EVENT_SUFFIX.length
            )
            LOG.info("Processing Chutney event $json")
            try {
                val jsonParser = JsonParser()
                val jsonElement = jsonParser.parse(json)
                if (jsonElement.isJsonObject) {
                    fireEvent(jsonElement.asJsonObject)
                } else {
                    LOG.warn("Unexpected Chutney event. Json root object expected. $json")
                }
            } catch (e: Exception) {
                LOG.warn("Cannot parse message from Chutney server:$json")
            }
            return true
        }
        return false
    }

    private fun fireEvent(json: JsonObject) {
        for (listener in myListeners) {
            listener.onEvent(json)
        }
    }

    fun dispose() {
        myListeners.clear()
        myTexts.clear()
    }

    companion object {
        private val LOG = Logger.getInstance(ChutneyServerOutputProcessor::class.java)
        private const val LIMIT = 1000
    }

    init {
        processHandler.addProcessListener(object : ProcessAdapter() {
            override fun onTextAvailable(event: ProcessEvent, outputType: Key<*>) {
                val text = event.text
                if (outputType !== ProcessOutputTypes.SYSTEM && outputType !== ProcessOutputTypes.STDERR) {
                    if (handleLineAsEvent(text)) {
                        return
                    }
                }
                synchronized(myTexts) {
                    myTexts.add(Pair.create(text, outputType))
                    if (myTexts.size > LIMIT) {
                        myTexts.poll()
                    }
                }
                for (listener in myListeners) {
                    listener.onOutputAvailable(text, outputType)
                }
            }
        })
    }
}

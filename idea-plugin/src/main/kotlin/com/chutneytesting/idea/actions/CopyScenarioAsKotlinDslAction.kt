package com.chutneytesting.idea.actions

import com.chutneytesting.idea.ChutneyUtil
import com.chutneytesting.idea.actions.converter.JsonSerializer
import com.chutneytesting.idea.actions.converter.v2.ScenarioV2
import com.chutneytesting.idea.actions.converter.v2.StepV2
import com.chutneytesting.idea.logger.EventDataLogger
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.diagnostic.Logger
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

class CopyScenarioAsKotlinDslAction : AnAction() {

    private val LOG = Logger.getInstance(UpdateLocalScenarioFromRemoteServer::class.java)

    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val file = event.getData(DataKeys.VIRTUAL_FILE) ?: return
        val psiFile = event.getData(LangDataKeys.PSI_FILE) ?: return
        val editor = event.getData(PlatformDataKeys.EDITOR) ?: return
        val document = editor.document
        if (ChutneyUtil.isChutneyV1Json(psiFile)) return
        val processJsonReference = ChutneyUtil.processJsonReference(psiFile.virtualFile)
        val scenarioV2 = ScenarioV2(JsonSerializer().toMap(processJsonReference))
        val id = ChutneyUtil.getChutneyScenarioIdFromFileName(file.name)
        try {
            val dsl = generateDsl(id, scenarioV2)
            Toolkit.getDefaultToolkit().systemClipboard.setContents(StringSelection(dsl), null)
            EventDataLogger.logInfo("Scenario file copied as Kotlin DSL with success.<br>", project)

        } catch (e: Exception) {
            LOG.debug(e.toString())
            EventDataLogger.logError(e.toString(), project)
        }
    }


}

private fun generateDsl(
    id: Int?,
    scenarioV2: ScenarioV2
): String {
    val dsl = """
                val `${scenarioV2.title()}` = Scenario(id=$id, title="${scenarioV2.title()}"){
                    ${scenarioV2.givens()
        .mapIndexed { index: Int, stepV2: StepV2 ->
            gwta(index, "Given") + "(\"${stepV2.description()}\")${stepV2.toDsl()}"
        }
        .joinToString(separator = "\n")}
                    When("${scenarioV2.`when`().description()}")${scenarioV2.`when`().toDsl()}
                    ${scenarioV2.thens()
        .mapIndexed { index: Int, stepV2: StepV2 ->
            gwta(
                index,
                "Then"
            ) + "(\"${stepV2.description()}\")${stepV2.toDsl()}"
        }
        .joinToString(separator = "\n")}
                }
            """.trimIndent()
    return dsl
}

private fun gwta(index: Int, gt: String) = if (index == 0) gt else "And"

private fun StepV2.toDsl(): String {
    if (this.isTaskStep) {
        val implementation = this.implementation()
        val type = implementation["type"] as String
        return when (type) {
            "context-put" -> mapContexPutTask(implementation)
            "http-get" -> mapHttpGetTask(implementation)
            "http-post" -> mapHttpPostTask(implementation)
            "amqp-clean-queues" -> mapAmqpCleanQueuesTask(implementation)
            "amqp-basic-consume" -> mapAmqpBasicConsumeTask(implementation)
            else -> "{\nTODO(\"Not yet implemented\")}"
        }
    } else {
        return this.subSteps()
            .joinToString(
                separator = "\n",
                prefix = "{\n",
                postfix = "}\n"
            ) { "Step(\"${it.description()}\") ${it.toDsl()}" }

    }
}

fun mapAmqpBasicConsumeTask(implementation: Map<String, Any?>): String {
    val inputs = implementation["inputs"] as Map<String, Any>?
    val selector = escapeKotlin((inputs?.get("selector") as String? ?: "")).wrapWithQuotes()
    val queueName =  escapeKotlin((inputs?.get("queue-name") as String? ?: "")).wrapWithQuotes()
    val nbMessages = inputs?.get("nb-messages") as Int? ?: 1
    val outputs = implementation["outputs"] as Map<String, Any>?
    return """{
        AmqpBasicConsumeTask(target= ${target(implementation)}, queueName= $queueName , nbMessages= $nbMessages,  timeout = "10 sec", selector = $selector, outputs = ${mapOfConstructor(
        outputs
    )})
    }"""
}

fun mapAmqpCleanQueuesTask(implementation: Map<String, Any?>): String {
    val inputs = implementation["inputs"] as Map<String, Any>?
    val queueNames = escapeKotlin(inputs?.get("queue-names") as String).wrapWithQuotes()
    return """{
        AmqpCleanQueuesTask(target= ${target(implementation)}, queueNames= ${queueNames})
    }"""
}

fun mapHttpGetTask(implementation: Map<String, Any?>): String {
    val inputs = implementation["inputs"] as Map<String, Any>?
    val headers = inputs?.get("headers") as Map<String, Any>?
    val outputs = implementation["outputs"] as Map<String, Any>?
    return """{
        HttpGetTask(target= ${target(implementation)}, uri= ${uri(implementation)} , headers= ${mapOfConstructor(headers)}, timeout = "2 sec", outputs = ${mapOfConstructor(
        outputs
    )}, strategy = null)
    }"""
}

fun mapHttpPostTask(implementation: Map<String, Any?>): String {
    val inputs = implementation["inputs"] as Map<String, Any>?
    val headers = inputs?.get("headers") as Map<String, Any>?
    val body = inputs?.get("body") as Map<String, Any>?
    val outputs = implementation["outputs"] as Map<String, Any>?
    return """{
        HttpPostTask(target= ${target(implementation)}, uri= ${uri(implementation)} , headers= ${mapOfConstructor(headers)}, body= ${mapOfConstructor(body)}, timeout = "2 sec", outputs = ${mapOfConstructor(outputs)}, strategy = null)
    }"""
}

fun uri(implementation: Map<String, Any?>): String {
    val inputs = implementation["inputs"] as Map<String, Any>
    return (inputs["uri"] as String).wrapWithQuotes()
}

private fun target(implementation: Map<String, Any?>): String = (implementation["target"] as String).wrapWithQuotes()

private fun String.wrapWithQuotes(): String {
    return "\"$this\""
}

private fun mapContexPutTask(implementation: Map<String, Any?>): String {
    val input = implementation["inputs"] as Map<String, Any>
    val entries = input["entries"] as Map<String, Any>
    return """{
        ContextPutTask(${mapOfConstructor(entries)})
    }"""
}

private fun mapOfConstructor(
    entries: Map<String, Any>?
): String {
    if (entries == null) {
        return "mapOf()"
    }
    return "mapOf(${entries.map {
        "\"${it.key}\" to \"${escapeKotlin(
            if (it.value is Map<*, *>) {
                JsonSerializer().toString(it.value as Map<*, *>)
            } else it.value.toString() //TODO check when is Int
        )}\""
    }.joinToString(",\n")})"
}

fun escapeKotlin(s: String): String {
    return s//.replace("'$", "'£")
        .replace("\${", "\\\${")
        .replace("\"", "\\\"")
        //.replace("'£", "'$")
}


fun main() {
    println(escapeKotlin("\"\${xx'$'}\""))
    //println("scenarioV2 = ${scenarioV2}")
    val generateDsl = generateDsl(123, ScenarioV2(JsonSerializer().toMap(scenarioV2)))
    println("generateDsl = ${generateDsl}")
}

val scenarioV2 =
    """
{
  "title": "Clean Queue",
  "givens": [
    {
      "description": "La liste des queues de RABBIT_DEV",
      "implementation": {
        "type": "http-get",
        "target": "ICOEUR_RABBIT_DEV_ADMIN",
        "inputs": {
          "uri": "/api/queues"
        },
        "outputs": {
          "queueNames": "'$'{#json(#body, '$.[*].name')}"
        }
      }
    }
  ],
  "when": {
    "description": "On purge toutes les queues",
    "implementation": {
      "type": "amqp-clean-queues",
      "target": "ICOEUR_RABBIT_DEV",
      "inputs": {
        "queue-names": "'$'{#queueNames}"
      }
    }
  }
}
"""

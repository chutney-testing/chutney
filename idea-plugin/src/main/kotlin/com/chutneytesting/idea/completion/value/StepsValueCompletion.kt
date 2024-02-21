package com.chutneytesting.idea.completion.value

import com.chutneytesting.idea.completion.CompletionHelper
import com.chutneytesting.idea.completion.value.model.StepValue
import com.chutneytesting.idea.completion.value.model.Value
import com.chutneytesting.idea.settings.ChutneySettings
import com.chutneytesting.kotlin.util.HttpClient
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.InsertHandler
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder

data class StepValueData(
    val id: String,
    val name: String,
    val usage: String,
    val task: String?,
    val steps: List<StepValueData>?
)

data class StepValueResult(val data: List<StepValueData>, val totalCount: Int)
class StepsValueCompletion(completionHelper: CompletionHelper, completionResultSet: CompletionResultSet) :
    ValueCompletion(completionHelper, completionResultSet) {

    override fun fill() {
        getSteps().forEach { this.addValue(it) }
    }

    private val steps = lazy {
        val serverInfo = ChutneySettings.getInstance().state.serverInfo()!!
        HttpClient.get<StepValueResult>(serverInfo,"/api/steps/v1?start=1&limit=1000&sort=name")
    }


    private fun getSteps(): List<Value> {
        return steps.value.data.map { StepValue(it) }
    }

    override fun addValue(value: Value) {
        if (completionHelper.isUniqueArrayStringValue(value.value)) {
            completionResultSet.addElement(create(value, completionHelper.createInsertStepValueHandler(value)))
        }
    }

    private fun create(value: Value, insertHandler: InsertHandler<LookupElement>): LookupElementBuilder {
        return LookupElementBuilder.create(value.value).withInsertHandler(insertHandler)
    }

}

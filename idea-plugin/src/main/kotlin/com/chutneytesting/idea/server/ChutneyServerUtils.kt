package com.chutneytesting.idea.server

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonSyntaxException
import com.intellij.openapi.application.ApplicationManager
import com.intellij.util.Consumer
import com.intellij.util.io.HttpRequests
import java.net.MalformedURLException
import java.net.URL

object ChutneyServerUtils {
    fun asyncFetchServerInfo(serverUrl: String, consumer: Consumer<in ChutneyServerFetchResult?>) {
        ApplicationManager.getApplication().executeOnPooledThread { consumer.consume(syncFetchServerInfo(serverUrl)) }
    }

    private fun syncFetchServerInfo(serverUrl: String): ChutneyServerFetchResult {
        try {
            URL(serverUrl)
        } catch (e: MalformedURLException) {
            return ChutneyServerFetchResult.fromErrorMessage("Malformed url: $serverUrl")
        }
        return try { //TODO cleanup
//return HttpRequests.request(serverUrl.replaceAll("/$", "") + "/cmd?listBrowsers").connect(new HttpRequests.RequestProcessor<ChutneyServerFetchResult>() {
            HttpRequests.request(serverUrl.replace("/$".toRegex(), "") + "/actuator/health")
                .useProxy(false) //.forceHttps(true)
                .connect { request: HttpRequests.Request ->
                    val badResponse = "Malformed server response received"
                    var jsonElement: JsonElement
                    try {
                        Gson().fromJson<Map<*, *>>(request.reader, MutableMap::class.java)
                    } catch (e: JsonSyntaxException) {
                        return@connect ChutneyServerFetchResult.fromErrorMessage(badResponse)
                    }
                    try {
                        return@connect ChutneyServerFetchResult.fromServerInfo(
                            ChutneyServerInfo(
                                serverUrl,
                                emptyList()
                            )
                        )
                    } catch (e: Exception) {
                        return@connect ChutneyServerFetchResult.fromErrorMessage(badResponse)
                    }
                }
        } catch (e: HttpRequests.HttpStatusException) {
            ChutneyServerFetchResult.fromErrorMessage("Incorrect server response status: " + e.statusCode)
        } catch (e: Exception) {
            ChutneyServerFetchResult.fromErrorMessage("Could not connect to $serverUrl")
        }
    }
}

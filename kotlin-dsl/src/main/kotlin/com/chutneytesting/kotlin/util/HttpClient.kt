/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.kotlin.util

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.paranamer.ParanamerModule
import org.apache.hc.client5.http.ContextBuilder
import org.apache.hc.client5.http.auth.AuthScope
import org.apache.hc.client5.http.auth.CredentialsProvider
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials
import org.apache.hc.client5.http.classic.methods.*
import org.apache.hc.client5.http.impl.auth.CredentialsProviderBuilder
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.client5.http.protocol.HttpClientContext
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory
import org.apache.hc.core5.http.ClassicHttpRequest
import org.apache.hc.core5.http.ContentType
import org.apache.hc.core5.http.HttpHost
import org.apache.hc.core5.http.io.entity.StringEntity
import org.apache.hc.core5.ssl.SSLContextBuilder
import java.io.BufferedInputStream
import java.io.InputStreamReader
import java.io.Reader

object HttpClient {

    enum class HttpMethod { POST, GET, PUT, PATCH, DELETE }

    inline fun <reified T> post(serverInfo: ChutneyServerInfo, query: String, body: String): T {
        return execute(serverInfo, query, HttpMethod.POST, body)
    }

    inline fun <reified T> get(serverInfo: ChutneyServerInfo, query: String): T {
        return execute(serverInfo, query, HttpMethod.GET, "")
    }

    inline fun <reified T> put(serverInfo: ChutneyServerInfo, query: String, body: String): T {
        return execute(serverInfo, query, HttpMethod.PUT, body)
    }

    inline fun <reified T> patch(serverInfo: ChutneyServerInfo, query: String, body: String): T {
        return execute(serverInfo, query, HttpMethod.PATCH, body)
    }

    inline fun <reified T> delete(serverInfo: ChutneyServerInfo, query: String, body: String): T {
        return execute(serverInfo, query, HttpMethod.DELETE, body)
    }

    inline fun <reified T> execute(
        serverInfo: ChutneyServerInfo,
        query: String,
        requestMethod: HttpMethod,
        body: String
    ): T {

        val proxyHost = serverInfo.proxyUri?.let { HttpHost(it.protocol, it.host, it.port) }
        val targetHost = HttpHost(serverInfo.uri.protocol, serverInfo.uri.host, serverInfo.uri.port)
        val credentialsProvider = buildCredentialProvider(serverInfo, targetHost, proxyHost)
        val httpClientContext = buildHttpClientContext(serverInfo, targetHost, proxyHost, credentialsProvider)
        val httpRequest = buildHttpRequest(requestMethod, query, body)
        val httpClient = buildHttpClient(proxyHost, credentialsProvider)

        httpClient.use { client ->
            val httpResponse = client.execute(targetHost, httpRequest, httpClientContext)

            httpResponse.use { response ->
                if (response.code >= 300) {
                    throw HttpClientException("Call to server returned status ${response.reasonPhrase}")
                }

                try {
                    val inputStream = BufferedInputStream(response.entity.content)
                    val reader: Reader = InputStreamReader(inputStream, Charsets.UTF_8)
                    val text = reader.readText()
                    if (text.isNotBlank()) {
                        val mapper = configureObjectMapper()
                        return mapper.readValue(text, object : TypeReference<T>() {})
                    } else {
                        return T::class.java.getConstructor().newInstance()
                    }
                } catch (e: Exception) {
                    throw HttpClientException(e)
                }
            }
        }
    }

    fun configureObjectMapper(): ObjectMapper {
        val stepImplModule = SimpleModule()
        return jacksonObjectMapper()
            .registerModule(stepImplModule)
            .registerModule(JavaTimeModule())
            .registerModule(ParanamerModule())
    }

    fun buildHttpClientContext(
        serverInfo: ChutneyServerInfo,
        targetHost: HttpHost,
        proxyHost: HttpHost?,
        credentialProvider: CredentialsProvider
    ): HttpClientContext {
        val contextBuilder = ContextBuilder.create()
        serverInfo.proxyUser?.let {
            contextBuilder.preemptiveBasicAuth(
                proxyHost,
                credentialProvider.getCredentials(AuthScope(proxyHost), null) as UsernamePasswordCredentials?
            )
        }
        contextBuilder.preemptiveBasicAuth(
                targetHost,
                credentialProvider.getCredentials(AuthScope(targetHost), null) as UsernamePasswordCredentials?
            )
        return contextBuilder.build()
    }

    fun buildHttpRequest(
        requestMethod: HttpMethod,
        uri: String,
        body: String
    ): ClassicHttpRequest {
        val httpRequest: ClassicHttpRequest
        when (requestMethod) {
            HttpMethod.POST -> {
                httpRequest = HttpPost(uri)
                httpRequest.entity = StringEntity(body, ContentType.APPLICATION_JSON)
            }

            HttpMethod.PUT -> {
                httpRequest = HttpPut(uri)
                httpRequest.entity = StringEntity(body, ContentType.APPLICATION_JSON)
            }

            HttpMethod.PATCH -> {
                httpRequest = HttpPatch(uri)
                httpRequest.entity = StringEntity(body, ContentType.APPLICATION_JSON)
            }

            HttpMethod.DELETE -> httpRequest = HttpDelete(uri)
            HttpMethod.GET -> httpRequest = HttpGet(uri)
        }
        return httpRequest
    }

    fun buildHttpClient(
        proxyHost: HttpHost?,
        credentialsProvider: CredentialsProvider
    ): CloseableHttpClient {
        val httpClientBuilder = HttpClients.custom()

        proxyHost?.let { httpClientBuilder.setProxy(proxyHost) }

        val sslContext = SSLContextBuilder.create().loadTrustMaterial { _, _ -> true }.build()
        val connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
            .setSSLSocketFactory(SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE))
            .build()

        httpClientBuilder
            .setConnectionManager(connectionManager)
            .setDefaultCredentialsProvider(credentialsProvider)

        return httpClientBuilder.build()
    }

    fun buildCredentialProvider(
        serverInfo: ChutneyServerInfo,
        targetHost: HttpHost,
        proxyHost: HttpHost?
    ): CredentialsProvider {
        val credentialsProvider = CredentialsProviderBuilder.create()
        serverInfo.proxyUser?.let {
            credentialsProvider.add(
                AuthScope(proxyHost),
                UsernamePasswordCredentials(serverInfo.proxyUser, serverInfo.proxyPassword?.toCharArray())
            )
        }
        credentialsProvider.add(
            AuthScope(targetHost),
            UsernamePasswordCredentials(serverInfo.user, serverInfo.password.toCharArray())
        )
        return credentialsProvider.build()
    }
}

class HttpClientException : RuntimeException {
    constructor(message: String?) : super(message)
    constructor(cause: Throwable?) : super(cause)
}

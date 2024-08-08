package com.chutneytesting.kotlin.util

import java.net.URL

data class ChutneyServerInfo(
    val url: String,
    val user: String,
    val password: String,
    val proxyUrl: String?,
    val proxyUser: String?,
    val proxyPassword: String?
) {
    constructor(url: String, user: String, password: String) :
        this(
            url,
            user,
            password,
            proxyUrlFromProperties(),
            proxyUserFromProperties(),
            proxyPasswordFromProperties()
        )

    val uri: URL = URL(url)
    val proxyUri: URL? = proxyUrl?.let { URL(it) }
}

private enum class ProxyProtocol { http, https }

private fun proxyUrlFromProperties(): String? {
    return ProxyProtocol.values()
        .firstNotNullOfOrNull {
            proxyUrlFromProperties(it, defaultPortResolver(it))
        }
}

private fun proxyUserFromProperties(): String? {
    return ProxyProtocol.values()
        .firstNotNullOfOrNull {
            proxyUserFromProperties(it)
        }
}

private fun proxyPasswordFromProperties(): String? {
    return ProxyProtocol.values()
        .firstNotNullOfOrNull {
            proxyPasswordFromProperties(it)
        }
}

private fun defaultPortResolver(scheme: ProxyProtocol): Int {
    return when (scheme) {
        ProxyProtocol.http -> 80
        ProxyProtocol.https -> 443
    }
}

private fun proxyUrlFromProperties(scheme: ProxyProtocol, defaultPort: Int): String? {
    val proxyHost = "${scheme}.proxyHost".sysProp()
    val proxyPort = "${scheme}.proxyPort".sysProp() ?: "$defaultPort"
    if (!proxyHost.isNullOrBlank()) {
        return "${scheme}://${proxyHost}:${proxyPort}"
    }
    return null
}

private fun proxyUserFromProperties(scheme: ProxyProtocol): String? {
    return "${scheme}.proxyUser".sysProp()
}

private fun proxyPasswordFromProperties(scheme: ProxyProtocol): String? {
    return "${scheme}.proxyPassword".sysProp()
}

private fun String.sysProp() = System.getProperty(this)

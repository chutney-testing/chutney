package com.chutneytesting.idea.completion.field.model

import com.google.common.collect.ImmutableList
import com.google.common.net.HttpHeaders

object ChutneyJsonFields {
    fun headers(): List<Field> {
        return ImmutableList.of(
            HeadersField(HttpHeaders.ACCEPT),
            HeadersField(HttpHeaders.ACCEPT_CHARSET),
            HeadersField(HttpHeaders.ACCEPT_ENCODING),
            HeadersField(HttpHeaders.ACCEPT_LANGUAGE),
            HeadersField(HttpHeaders.ACCEPT_RANGES),
            HeadersField(HttpHeaders.AGE),
            HeadersField(HttpHeaders.ALLOW),
            HeadersField(HttpHeaders.AUTHORIZATION),
            HeadersField(HttpHeaders.CACHE_CONTROL),
            HeadersField(HttpHeaders.CONNECTION),
            HeadersField(HttpHeaders.CONTENT_ENCODING),
            HeadersField(HttpHeaders.CONTENT_LANGUAGE),
            HeadersField(HttpHeaders.CONTENT_LENGTH),
            HeadersField(HttpHeaders.CONTENT_LOCATION),
            HeadersField(HttpHeaders.CONTENT_MD5),
            HeadersField(HttpHeaders.CONTENT_RANGE),
            HeadersField(HttpHeaders.CONTENT_TYPE),
            HeadersField(HttpHeaders.DATE),
            HeadersField(HttpHeaders.ETAG),
            HeadersField(HttpHeaders.EXPECT),
            HeadersField(HttpHeaders.EXPIRES),
            HeadersField(HttpHeaders.FROM),
            HeadersField(HttpHeaders.HOST),
            HeadersField(HttpHeaders.IF_MATCH),
            HeadersField(HttpHeaders.IF_MODIFIED_SINCE),
            HeadersField(HttpHeaders.IF_NONE_MATCH),
            HeadersField(HttpHeaders.IF_RANGE),
            HeadersField(HttpHeaders.IF_UNMODIFIED_SINCE),
            HeadersField(HttpHeaders.LAST_MODIFIED),
            HeadersField(HttpHeaders.LOCATION),
            HeadersField(HttpHeaders.MAX_FORWARDS),
            HeadersField(HttpHeaders.PRAGMA),
            HeadersField(HttpHeaders.PROXY_AUTHENTICATE),
            HeadersField(HttpHeaders.PROXY_AUTHORIZATION),
            HeadersField(HttpHeaders.RANGE),
            HeadersField(HttpHeaders.REFERER),
            HeadersField(HttpHeaders.RETRY_AFTER),
            HeadersField(HttpHeaders.SERVER),
            HeadersField(HttpHeaders.TE),
            HeadersField(HttpHeaders.TRAILER),
            HeadersField(HttpHeaders.TRANSFER_ENCODING),
            HeadersField(HttpHeaders.UPGRADE),
            HeadersField(HttpHeaders.USER_AGENT),
            HeadersField(HttpHeaders.VARY),
            HeadersField(HttpHeaders.VIA),
            HeadersField(HttpHeaders.WARNING),
            HeadersField(HttpHeaders.WWW_AUTHENTICATE)
        )
    }
}

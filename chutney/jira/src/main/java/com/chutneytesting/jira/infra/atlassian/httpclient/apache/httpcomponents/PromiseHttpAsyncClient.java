/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */
package com.chutneytesting.jira.infra.atlassian.httpclient.apache.httpcomponents;

import io.atlassian.util.concurrent.Promise;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.protocol.HttpContext;

/**
 * @see com.atlassian.httpclient.apache.httpcomponents.PromiseHttpAsyncClient
 */
interface PromiseHttpAsyncClient {
    Promise<HttpResponse> execute(HttpUriRequest request, HttpContext context);
}

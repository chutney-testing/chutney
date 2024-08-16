/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */
package com.chutneytesting.jira.infra.atlassian.httpclient.apache.httpcomponents;

import static com.google.common.base.Preconditions.checkNotNull;

import com.atlassian.event.api.EventPublisher;
import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.executor.ThreadLocalContextManager;
import com.chutneytesting.jira.infra.atlassian.httpclient.api.factory.HttpClientOptions;
import com.google.common.annotations.VisibleForTesting;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.DisposableBean;
// CHANGE - End

// CHANGE - Begin
/**
 * <pre>
 *  Changes :
 *   - Use local HttpClientOptions class
 *  </pre>
 * @see com.atlassian.httpclient.apache.httpcomponents.DefaultHttpClientFactory
 */
//public final class DefaultHttpClientFactory<C> implements HttpClientFactory, DisposableBean {
public final class DefaultHttpClientFactory<C> implements DisposableBean {
// CHANGE - End
    private final EventPublisher eventPublisher;
    private final ApplicationProperties applicationProperties;
    private final ThreadLocalContextManager<C> threadLocalContextManager;
    private final Set<ApacheAsyncHttpClient> httpClients = new CopyOnWriteArraySet<>();

    public DefaultHttpClientFactory(
            @Nonnull EventPublisher eventPublisher,
            @Nonnull ApplicationProperties applicationProperties,
            @Nonnull ThreadLocalContextManager<C> threadLocalContextManager) {
        this.eventPublisher = checkNotNull(eventPublisher);
        this.applicationProperties = checkNotNull(applicationProperties);
        this.threadLocalContextManager = checkNotNull(threadLocalContextManager);
    }

// CHANGE - Begin
//    @Override
// CHANGE - End
    @Nonnull
    public HttpClient create(@Nonnull HttpClientOptions options) {
        return doCreate(options, threadLocalContextManager);
    }

// CHANGE - Begin
//    @Override
// CHANGE - End
    @Nonnull
    public <C> HttpClient create(@Nonnull HttpClientOptions options, @Nonnull ThreadLocalContextManager<C> threadLocalContextManager) {
        return doCreate(options, threadLocalContextManager);
    }

// CHANGE - Begin
//    @Override
// CHANGE - End
    public void dispose(@Nonnull final HttpClient httpClient) throws Exception {
        if (httpClient instanceof ApacheAsyncHttpClient) {
            final ApacheAsyncHttpClient client = (ApacheAsyncHttpClient) httpClient;
            if (httpClients.remove(client)) {
                client.destroy();
            } else {
                throw new IllegalStateException("Client is already disposed");
            }
        } else {
            throw new IllegalArgumentException("Given client is not disposable");
        }
    }

    private <C> HttpClient doCreate(@Nonnull HttpClientOptions options, ThreadLocalContextManager<C> threadLocalContextManager) {
        checkNotNull(options);
        final ApacheAsyncHttpClient<C> httpClient = new ApacheAsyncHttpClient<>(eventPublisher, applicationProperties, threadLocalContextManager, options);
        httpClients.add(httpClient);
        return httpClient;
    }

    @Override
    public void destroy() throws Exception {
        for (ApacheAsyncHttpClient httpClient : httpClients) {
            httpClient.destroy();
        }
    }

    @VisibleForTesting
    @Nonnull
    Iterable<ApacheAsyncHttpClient> getHttpClients() {
        return httpClients;
    }
}

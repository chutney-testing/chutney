/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */
package com.chutneytesting.jira.infra.atlassian.httpclient.apache.httpcomponents;

import static com.google.common.base.Preconditions.checkNotNull;

import com.atlassian.sal.api.executor.ThreadLocalContextManager;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.util.concurrent.Promise;
import io.atlassian.util.concurrent.Promises;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nonnull;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.protocol.HttpContext;

/**
 * @see com.httpclient.apache.httpcomponents.SettableFuturePromiseHttpPromiseAsyncClient
 */
final class SettableFuturePromiseHttpPromiseAsyncClient<C> implements PromiseHttpAsyncClient {
    private final HttpAsyncClient client;
    private final ThreadLocalContextManager<C> threadLocalContextManager;
    private final Executor executor;

    SettableFuturePromiseHttpPromiseAsyncClient(HttpAsyncClient client, ThreadLocalContextManager<C> threadLocalContextManager, Executor executor) {
        this.client = checkNotNull(client);
        this.threadLocalContextManager = checkNotNull(threadLocalContextManager);
        this.executor = new ThreadLocalDelegateExecutor<>(threadLocalContextManager, executor);
    }

    @Override
    public Promise<HttpResponse> execute(HttpUriRequest request, HttpContext context) {
        final CompletableFuture<HttpResponse> future = new CompletableFuture<>();
        client.execute(request, context, new ThreadLocalContextAwareFutureCallback<C, HttpResponse>(threadLocalContextManager) {
            @Override
            void doCompleted(final HttpResponse httpResponse) {
                executor.execute(() -> future.complete(httpResponse));
            }

            @Override
            void doFailed(final Exception ex) {
                executor.execute(() -> future.completeExceptionally(ex));
            }

            @Override
            void doCancelled() {
                final TimeoutException timeoutException = new TimeoutException();
                executor.execute(() -> future.completeExceptionally(timeoutException));
            }
        });
        return Promises.forCompletionStage(future);
    }

    @VisibleForTesting
    static <C> void runInContext(ThreadLocalContextManager<C> threadLocalContextManager, C threadLocalContext, ClassLoader contextClassLoader, Runnable runnable) {
        final C oldThreadLocalContext = threadLocalContextManager.getThreadLocalContext();
        final ClassLoader oldCcl = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
            threadLocalContextManager.setThreadLocalContext(threadLocalContext);
            runnable.run();
        } finally {
            threadLocalContextManager.setThreadLocalContext(oldThreadLocalContext);
            Thread.currentThread().setContextClassLoader(oldCcl);
        }
    }

    private static abstract class ThreadLocalContextAwareFutureCallback<C, HttpResponse> implements FutureCallback<HttpResponse> {
        private final ThreadLocalContextManager<C> threadLocalContextManager;
        private final C threadLocalContext;
        private final ClassLoader contextClassLoader;

        private ThreadLocalContextAwareFutureCallback(ThreadLocalContextManager<C> threadLocalContextManager) {
            this.threadLocalContextManager = checkNotNull(threadLocalContextManager);
            this.threadLocalContext = threadLocalContextManager.getThreadLocalContext();
            this.contextClassLoader = Thread.currentThread().getContextClassLoader();
        }

        abstract void doCompleted(HttpResponse response);

        abstract void doFailed(Exception ex);

        abstract void doCancelled();

        @Override
        public final void completed(final HttpResponse response) {
            runInContext(threadLocalContextManager, threadLocalContext, contextClassLoader, () -> doCompleted(response));
        }

        @Override
        public final void failed(final Exception ex) {
            runInContext(threadLocalContextManager, threadLocalContext, contextClassLoader, () -> doFailed(ex));
        }

        @Override
        public final void cancelled() {
            runInContext(threadLocalContextManager, threadLocalContext, contextClassLoader, this::doCancelled);
        }
    }

    private static final class ThreadLocalDelegateExecutor<C> implements Executor {
        private final Executor delegate;
        private final ThreadLocalContextManager<C> manager;

        ThreadLocalDelegateExecutor(ThreadLocalContextManager<C> manager, Executor delegate) {
            this.delegate = checkNotNull(delegate);
            this.manager = checkNotNull(manager);
        }

        public void execute(@Nonnull final Runnable runnable) {
            delegate.execute(new ThreadLocalDelegateRunnable<>(manager, runnable));
        }
    }

    private static final class ThreadLocalDelegateRunnable<C> implements Runnable {
        private final C context;
        private final Runnable delegate;
        private final ClassLoader contextClassLoader;
        private final ThreadLocalContextManager<C> manager;

        ThreadLocalDelegateRunnable(ThreadLocalContextManager<C> manager, Runnable delegate) {
            this.delegate = delegate;
            this.manager = manager;
            this.context = manager.getThreadLocalContext();
            this.contextClassLoader = Thread.currentThread().getContextClassLoader();
        }

        public void run() {
            runInContext(manager, context, contextClassLoader, delegate);
        }
    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting;

import java.io.Closeable;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionRoutingDataSource extends AbstractRoutingDataSource implements DisposableBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionRoutingDataSource.class);

    @Override
    protected Object determineCurrentLookupKey() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly() ?
            DataSourceType.READ_ONLY :
            DataSourceType.READ_WRITE;
    }

    @Override
    public void destroy() {
        getResolvedDataSources().values().stream()
            .filter(v -> v instanceof Closeable)
            .map(v -> (Closeable) v)
            .forEach(ds -> {
                try {
                    ds.close();
                } catch (IOException e) {
                    LOGGER.warn("Datasource {} cannot be closed.", ds, e);
                }
            });
    }
}

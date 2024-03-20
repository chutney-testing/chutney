/*
 *  Copyright 2017-2023 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

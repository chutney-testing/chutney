/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.dataset;

public class InvalidExternalDatasetException extends RuntimeException {

    public InvalidExternalDatasetException(String id) {
        super("Invalid external dataset [" + id + "]: both id and inline data are present");
    }

    public InvalidExternalDatasetException(String id, Throwable throwable) {
        super("Invalid external dataset [" + id + "]: both id and inline data are present", throwable);
    }

    public InvalidExternalDatasetException() {
        super("Invalid external dataset : both id and inline data are missing, one of them is mandatory");
    }

    public InvalidExternalDatasetException(Throwable throwable) {
        super("Invalid external dataset : both id and inline data are missing, one of them is mandatory", throwable);
    }
}

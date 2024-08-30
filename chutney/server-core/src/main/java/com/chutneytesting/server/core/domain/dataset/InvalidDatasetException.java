/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.dataset;

public class InvalidDatasetException extends RuntimeException {

    public InvalidDatasetException(String id) {
        super("Invalid dataset [" + id + "]: both id and inline data are present");
    }

    public InvalidDatasetException(String id, Throwable throwable) {
        super("Invalid dataset [" + id + "]: both id and inline data are present", throwable);
    }

    public InvalidDatasetException() {
        super("Invalid dataset : both id and inline data are missing, one of them is mandatory");
    }

    public InvalidDatasetException(Throwable throwable) {
        super("Invalid dataset : both id and inline data are missing, one of them is mandatory", throwable);
    }
}

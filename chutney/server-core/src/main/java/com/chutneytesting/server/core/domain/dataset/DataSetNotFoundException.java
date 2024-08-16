/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.dataset;

public class DataSetNotFoundException extends RuntimeException {
    public DataSetNotFoundException(String id) {
        super("Dataset [" + id + "] could not be found");
    }

  public DataSetNotFoundException(String id, Throwable throwable) {
    super("Dataset [" + id + "] could not be found", throwable);
  }
}

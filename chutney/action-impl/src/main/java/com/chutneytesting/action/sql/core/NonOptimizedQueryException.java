/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.sql.core;

public class NonOptimizedQueryException extends RuntimeException {

    public NonOptimizedQueryException() {
        super("Query fetched too many rows. Please try to refine your query.");
    }

}

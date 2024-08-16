/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.admin.domain;

public interface DBVacuum {

    /**
     * Try to compact database
     */
    VacuumReport vacuum();

    /**
     * Compute current database size in bytes
     *
     * @return The size in bytes
     */
    long size();

    record VacuumReport(Long beforeSize, Long afterSize) {
    }
}

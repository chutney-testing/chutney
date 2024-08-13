/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.sql.core;


import com.chutneytesting.action.spi.injectable.Target;

public interface SqlClientFactory {

    SqlClient create(Target target);

}

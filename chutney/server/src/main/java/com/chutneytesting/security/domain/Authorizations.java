/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.domain;

import com.chutneytesting.server.core.domain.security.UserRoles;

public interface Authorizations {

    UserRoles read();

    void save(UserRoles userRoles);
}

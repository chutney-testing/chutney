/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.design.domain.plugins.linkifier;

import java.util.List;

public interface Linkifiers {

    List<Linkifier> getAll();

    Linkifier add(Linkifier linkifier);

    void remove(String id);
}

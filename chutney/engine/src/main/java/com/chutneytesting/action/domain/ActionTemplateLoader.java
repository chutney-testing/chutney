/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.domain;

import java.util.List;

/**
 * Simple loader of {@link ActionTemplate}.
 *
 * @see DefaultActionTemplateRegistry
 */
@FunctionalInterface
public interface ActionTemplateLoader {

    List<ActionTemplate> load();
}

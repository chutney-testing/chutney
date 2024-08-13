/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.domain;

import com.chutneytesting.scenario.domain.gwt.GwtScenario;

public interface GwtScenarioMarshaller {

    String serialize(GwtScenario scenario);

    GwtScenario deserialize(String title, String description, String blob);

}

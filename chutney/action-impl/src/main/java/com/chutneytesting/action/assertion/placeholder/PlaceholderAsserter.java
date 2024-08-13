/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.assertion.placeholder;

import com.chutneytesting.action.spi.injectable.Logger;

public interface PlaceholderAsserter {

    boolean canApply(String value);

    boolean assertValue(Logger logger, Object actual, Object expected);

}

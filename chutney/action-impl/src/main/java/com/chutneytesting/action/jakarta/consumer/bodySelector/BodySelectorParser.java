/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.jakarta.consumer.bodySelector;

import java.util.Optional;

public interface BodySelectorParser {

    String description();

    /**
     * @throws IllegalArgumentException if the selector matches the parser but {@link BodySelector} cannot be built nonetheless
     */
    Optional<BodySelector> tryParse(String selector) throws IllegalArgumentException;
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.execution.engine.parameterResolver;

@SuppressWarnings("serial")
class InputNameMandatoryException extends RuntimeException {

    public InputNameMandatoryException() {
        super("Input name is always mandatory");
    }
}

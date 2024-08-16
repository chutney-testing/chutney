/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.execution.engine.evaluation;

@SuppressWarnings("serial")
public class EvaluationException extends RuntimeException {

    EvaluationException(String message) {
        super(message);
    }

    EvaluationException(String message, Exception cause) {
        super(message, cause);
    }
}

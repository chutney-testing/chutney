/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.domain.parameter;

/**
 * A {@link ParameterResolver} produce a value that can be used for the given {@link Parameter}.
 *
 * @see Parameter
 */
public interface ParameterResolver {

    /**
     * @return true if the current {@link ParameterResolver} can supply a value for the given {@link Parameter}
     */
    boolean canResolve(Parameter parameter);

    /**
     * @return a value adapted to the given {@link Parameter}
     */
    Object resolve(Parameter parameter);
}

/*
 * Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

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

package com.chutneytesting.engine.domain.execution.engine.parameterResolver;

import com.chutneytesting.action.domain.parameter.Parameter;
import com.chutneytesting.action.domain.parameter.ParameterResolver;
import java.util.Map;

public class ContextParameterResolver implements ParameterResolver {

    private final Map<String, Object> inputs;

    public ContextParameterResolver(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    @Override
    public boolean canResolve(Parameter parameter) {
        return parameter.annotations().isEmpty() && parameter.rawType().equals(Map.class);
    }

    @Override
    public Object resolve(Parameter parameter) {
        return inputs;
    }

}

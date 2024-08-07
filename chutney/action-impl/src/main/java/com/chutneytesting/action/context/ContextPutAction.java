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

package com.chutneytesting.action.context;

import static java.util.Collections.emptyMap;
import static java.util.Optional.ofNullable;

import com.chutneytesting.action.spi.Action;
import com.chutneytesting.action.spi.ActionExecutionResult;
import com.chutneytesting.action.spi.injectable.Input;
import com.chutneytesting.action.spi.injectable.Logger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ContextPutAction implements Action {

    private final Logger logger;
    private final Map<String, Object> entries;

    public ContextPutAction(Logger logger, @Input("entries") Map<String, Object> entries) {
        this.logger = logger;
        this.entries = ofNullable(entries).orElse(emptyMap());
    }

    @Override
    public ActionExecutionResult execute() {
        entries.forEach((key, value) -> logger.info("Adding to context " + key + " = " + prettyLog(value) + " " + logClassType(value)));
        return ActionExecutionResult.ok(entries);
    }

    private String prettyLog(Object value) {
        if (value == null) {
            return "null";
        } else if (value instanceof String) {
            return value.toString();
        } else if (value instanceof Object[] objects) {
            return Arrays.toString(objects);
        } else if (value instanceof List list) {
            return Arrays.toString(list.toArray());
        } else if (value instanceof Map map) {
            return Arrays.toString(map.entrySet().toArray());
        } else {
            return value.toString();
        }
    }

    private String logClassType(Object value) {
        return value != null ? "(" + value.getClass().getSimpleName() + ")" : "";
    }
}

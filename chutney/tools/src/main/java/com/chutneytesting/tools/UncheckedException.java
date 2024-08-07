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

package com.chutneytesting.tools;

/**
 * Specific {@link RuntimeException} thrown when checked {@link Exception} occurs in <b>Throwing</b>Functions.<br>
 * Checked {@link Exception} is set as cause.
 *
 * @see ThrowingFunction#toUnchecked(ThrowingFunction)
 */
@SuppressWarnings("serial")
public class UncheckedException extends RuntimeException {

    private UncheckedException(Exception checkedException) {
        super("Occurred in silenced function", checkedException);
    }

    public static RuntimeException throwUncheckedException(Exception e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        } else {
            return new UncheckedException(e);
        }
    }
}

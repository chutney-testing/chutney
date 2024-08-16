/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.glacio.domain.parser;

import java.util.HashMap;
import java.util.Map;

public class ParsingContext {

    public enum PARSING_CONTEXT_KEYS { ENVIRONMENT }

    public final Map<PARSING_CONTEXT_KEYS, String> values = new HashMap<>();

}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.glacio.domain.parser;

import com.github.fridujo.glacio.model.Step;

public interface GlacioStepParser<T> {

    T parseGlacioStep(ParsingContext context, Step step);

}

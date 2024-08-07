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

package com.chutneytesting.glacio.util;


import static com.chutneytesting.glacio.util.ParserClasspathReader.createGlacioParsers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chutneytesting.glacio.domain.parser.DebugParser;
import com.chutneytesting.glacio.domain.parser.IParseExecutableStep;
import com.chutneytesting.glacio.domain.parser.NoGlacioParser;
import com.chutneytesting.glacio.domain.parser.SuccessParser;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

class ParserClasspathReaderTest {

    private final Locale testLang = new Locale("tt", "TT");

    @Test
    public void should_map_a_locale_keyword_pair_to_a_unique_executable_step_parser() {
        Map<Pair<Locale, String>, IParseExecutableStep> glacioParsers = createGlacioParsers("META-INF/extension/chutney.glacio.parsers");
        assertThat(glacioParsers.get(Pair.of(testLang, "DEBUG"))).isInstanceOf(DebugParser.class);
        assertThat(glacioParsers.get(Pair.of(testLang, "DBG"))).isInstanceOf(DebugParser.class);
        assertThat(glacioParsers.get(Pair.of(testLang, "SUCCESS"))).isInstanceOf(SuccessParser.class);
        assertThat(glacioParsers.get(Pair.of(testLang, "SUCC"))).isInstanceOf(SuccessParser.class);
    }

    @Test
    public void should_ignore_declared_parsers_which_dont_implements_interface() {
        List<IParseExecutableStep> testParsers = getIParseExecutableSteps().stream()
            .filter(parser -> parser instanceof NoGlacioParser)
            .collect(Collectors.toList());
        assertThat(testParsers).isEmpty();
    }

    @Test
    public void should_not_allow_parser_with_same_keyword() {
        assertThatThrownBy(() -> createGlacioParsers("META-INF/extension/chutney.glacio.wrong.parsers")).isInstanceOf(IllegalArgumentException.class);
    }

    private List<IParseExecutableStep> getIParseExecutableSteps() {
        Map<Pair<Locale, String>, IParseExecutableStep> glacioParsers = createGlacioParsers("META-INF/extension/chutney.glacio.parsers");
        return new ArrayList<>(glacioParsers.values());
    }
}

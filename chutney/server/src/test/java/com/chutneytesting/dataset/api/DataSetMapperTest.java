/*
 * Copyright 2017-2023 Enedis
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

package com.chutneytesting.dataset.api;

import static org.assertj.core.api.Assertions.assertThat;

import com.chutneytesting.server.core.domain.tools.ui.ImmutableKeyValue;
import com.chutneytesting.server.core.domain.tools.ui.KeyValue;
import java.util.List;
import java.util.function.Predicate;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Builders;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.time.api.DateTimes;

class DataSetMapperTest {

    @Property(tries = 100)
    void map_dto_back_and_forth(@ForAll("validDto") DataSetDto dto) {
        DataSetDto mapDto = DataSetMapper.toDto(DataSetMapper.fromDto(dto));
        assertThat(mapDto.id()).isEqualTo(dto.id());
        assertThat(mapDto.name()).isEqualTo(dto.name());
        assertThat(mapDto.description()).isEqualTo(dto.description());
        assertThat(mapDto.lastUpdated()).isEqualTo(dto.lastUpdated());
        assertThat(mapDto.constants()).containsExactlyInAnyOrderElementsOf(dto.constants());
        assertThat(
            mapDto.datatable().stream().flatMap(List::stream).toList()
        ).containsExactlyInAnyOrderElementsOf(
            dto.datatable().stream().flatMap(List::stream).toList()
        );
    }

    @Provide
    @SuppressWarnings("unused")
    private Arbitrary<DataSetDto> validDto() {
        return Builders.withBuilder(ImmutableDataSetDto::builder)
            .use(Arbitraries.strings().ofMinLength(1)).in(ImmutableDataSetDto.Builder::id)
            .use(arbitraryName()).in(ImmutableDataSetDto.Builder::name)
            .use(Arbitraries.strings()).in(ImmutableDataSetDto.Builder::description)
            .use(DateTimes.instants()).in(ImmutableDataSetDto.Builder::lastUpdated)
            .use(keyValueArbitrary().list()).in(ImmutableDataSetDto.Builder::constants)
            .use(valueListArbitrary(arbitraryKey().list().sample()).list()).in(ImmutableDataSetDto.Builder::datatable)
            .build(ImmutableDataSetDto.Builder::build);
    }

    Predicate<String> strippedNotBlank = Predicate.not(s -> s.strip().isBlank());
    Predicate<String> strippedSameLength = s -> s.length() == s.strip().length();
    Predicate<String> withoutTwoConsecutiveSpaces = Predicate.not(s -> s.matches(" {2}"));
    Predicate<String> trimmedNotBlank = Predicate.not(s -> s.trim().isBlank());
    Predicate<String> trimmedSameLength = s -> s.length() == s.trim().length();

    private Arbitrary<String> arbitraryName() {
        return Arbitraries.strings().ofMinLength(1)
            .filter(strippedNotBlank)
            .filter(strippedSameLength)
            .filter(withoutTwoConsecutiveSpaces);
    }

    private Arbitrary<String> arbitraryKey() {
        return Arbitraries.strings().ofMinLength(1)
            .filter(trimmedNotBlank)
            .filter(trimmedSameLength);
    }

    private Arbitrary<String> arbitraryValue() {
        return Arbitraries.strings()
            .filter(Predicate.not(s -> s.trim().isBlank()))
            .filter(s -> s.length() == s.trim().length());
    }

    private Arbitrary<KeyValue> keyValueArbitrary() {
        return Builders.withBuilder(ImmutableKeyValue::builder)
            .use(arbitraryKey()).in(ImmutableKeyValue.Builder::key)
            .use(arbitraryValue()).in(ImmutableKeyValue.Builder::value)
            .build(ImmutableKeyValue.Builder::build);
    }

    private Arbitrary<? extends List<KeyValue>> valueListArbitrary(List<String> keys) {
        Arbitrary<String> valueArbitrary = arbitraryValue();
        return Arbitraries.just(
            keys.stream()
                .map(k -> (KeyValue) ImmutableKeyValue.builder().key(k).value(valueArbitrary.sample()).build())
                .toList()
        );
    }
}

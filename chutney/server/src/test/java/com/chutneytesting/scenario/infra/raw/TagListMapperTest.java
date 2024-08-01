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

package com.chutneytesting.scenario.infra.raw;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;

public class TagListMapperTest {

    @Test
    public void map_null_string_to_empty_list() {
        List<String> actual = TagListMapper.tagsStringToList(null);
        assertThat(actual).isEmpty();
    }

    @Test
    public void map_null_string_to_empty_set() {
        Set<String> actual = TagListMapper.tagsStringToSet(null);
        assertThat(actual).isEmpty();
    }

    @Test
    public void map_null_list_to_empty_string() {
        String actual = TagListMapper.tagsToString(null);
        assertThat(actual).isEqualTo("");
    }

    @Test
    public void map_string_to_list_splitting_by_comma_separator() {
        List<String> actual = TagListMapper.tagsStringToList("  T1  , T2   ,  T3  ");
        assertThat(actual).containsExactly("T1", "T2", "T3");
    }

    @Test
    public void map_string_to_set_splitting_by_comma_separator() {
        Set<String> actual = TagListMapper.tagsStringToSet("  T1  , T2   ,  T3  ");
        assertThat(actual).containsExactly("T1", "T2", "T3");
    }

    @Test
    public void maps_tags_collection_to_string_joining_with_comma_separator() {
        String actualList = TagListMapper.tagsToString(List.of("  T1", " T2 "));
        String actualSet = TagListMapper.tagsToString(Set.of("  T1", " T2 "));
        assertThat(actualList).isEqualTo("T1,T2");
        assertThat(actualSet)
            .matches(Pattern.compile("T[12],T[12]"));
    }
}

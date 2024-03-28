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

package com.chutneytesting.scenario.infra.raw;

import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;

public final class TagListMapper {
    private static final String TAGS_SEPARATOR = ",";
    public static final String TAGS_SPLIT_REG_EX = "\\s*" + TAGS_SEPARATOR + "\\s*";

    public static List<String> tagsStringToList(String tags) {
        return Stream.of(splitTagsString(tags))
            .filter(StringUtils::isNotBlank)
            .map(String::trim)
            .collect(toList());
    }

    public static Set<String> tagsStringToSet(String tags) {
        return Stream.of(splitTagsString(tags))
            .filter(StringUtils::isNotBlank)
            .map(String::trim)
            .collect(toSet());
    }

    public static String tagsToString(Collection<String> tags) {
        return ofNullable(tags).stream()
            .flatMap(Collection::stream)
            .filter(StringUtils::isNotBlank)
            .map(String::trim)
            .collect(joining(TAGS_SEPARATOR));
    }

    private static String[] splitTagsString(String tags) {
        return ofNullable(tags).orElse("").split(TAGS_SPLIT_REG_EX);
    }
}

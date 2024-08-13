/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
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

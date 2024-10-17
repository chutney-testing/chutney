/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.dataset.api;

import static java.util.Collections.emptyList;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.util.List;
import org.immutables.value.Value;
import org.springframework.data.util.Pair;

@Value.Immutable
@JsonSerialize(as = ImmutableDataSetUsageDto.class)
@JsonDeserialize(as = ImmutableDataSetUsageDto.class)
@Value.Style(jdkOnly = true)
public interface DataSetUsageDto {
    @Value.Default()
    default DataSetDto dataset() {
        return null;
    }

    @Value.Default()
    default List<String> scenarioUsage() {
        return emptyList();
    }

    @Value.Default()
    default List<String> campaignUsage() {
        return emptyList();
    }

    @Value.Default()
    default List<Pair<String, String>> scenarioInCampaignUsage() {
        return emptyList();
    }
}

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

package com.chutneytesting.server.core.domain.execution;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import com.chutneytesting.server.core.domain.dataset.DataSet;
import com.chutneytesting.server.core.domain.scenario.TestCase;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import com.google.common.collect.Streams;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public class ExecutionRequest {

    public final TestCase testCase;
    public final String environment;
    public final String userId;
    public final DataSet dataset;
    public final CampaignExecution campaignExecution;
    public final List<String> tags;

    public ExecutionRequest(TestCase testCase, String environment, String userId, DataSet dataset, CampaignExecution campaignExecution, List<String> extraTags) {
        this.testCase = testCase;
        this.environment = environment;
        this.userId = userId;
        this.dataset = dataset;
        this.campaignExecution = campaignExecution;
        this.tags = tags(extraTags);
    }

    public ExecutionRequest(TestCase testCase, String environment, String userId, DataSet dataset, CampaignExecution campaignExecution) {
        this(testCase, environment, userId, dataset, campaignExecution, emptyList());
    }
    public ExecutionRequest(TestCase testCase, String environment, String userId, DataSet dataset) {
        this(testCase, environment, userId, dataset, null, emptyList());
    }

    public ExecutionRequest(TestCase testCase, String environment, String userId) {
        this(testCase, environment, userId, DataSet.NO_DATASET, null, emptyList());
    }

    private List<String> tags(List<String> extraTags) {
        return Streams.concat(
                ofNullable(testCase).map(tc -> tc.metadata().tags().stream()).orElse(Stream.empty()),
                ofNullable(dataset).stream().flatMap(ds -> ofNullable(ds.tags).stream().flatMap(Collection::stream)),
                ofNullable(extraTags).map(HashSet::new).stream().flatMap(Collection::stream)
            )
            .toList();
    }
}

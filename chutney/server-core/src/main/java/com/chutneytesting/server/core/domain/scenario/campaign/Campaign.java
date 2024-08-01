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

package com.chutneytesting.server.core.domain.scenario.campaign;

import java.util.ArrayList;
import java.util.List;

public class Campaign {

    public final Long id;
    public final String title;
    public final String description;
    public final List<CampaignScenario> scenarios;
    public final boolean parallelRun;
    public final boolean retryAuto;
    public final String externalDatasetId;
    public final List<String> tags;

    private String environment;

    public Campaign(Long id,
                    String title,
                    String description,
                    List<CampaignScenario> scenarios,
                    String environment,
                    boolean parallelRun,
                    boolean retryAuto,
                    String externalDatasetId,
                    List<String> tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.scenarios = initListNullOrEmpty(scenarios);
        this.parallelRun = parallelRun;
        this.retryAuto = retryAuto;
        this.environment = environment;
        this.externalDatasetId = externalDatasetId;
        this.tags = tags;
    }

    public void executionEnvironment(String environment) {
        this.environment = environment;
    }

    public String executionEnvironment() {
        return this.environment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Campaign campaign = (Campaign) o;
        return id.equals(campaign.id) &&
            title.equals(campaign.title) &&
            description.equals(campaign.description) &&
            scenarios.equals(campaign.scenarios) &&
            parallelRun == campaign.parallelRun &&
            retryAuto == campaign.retryAuto &&
            environment.equals(campaign.environment) &&
            externalDatasetId.equals(campaign.externalDatasetId) &&
            tags.equals(campaign.tags);
    }

    private <T> List<T> initListNullOrEmpty(List<T> list) {
        if (list != null && !list.isEmpty()) {
            return list;
        }
        return new ArrayList<>();
    }

    public record CampaignScenario(String scenarioId, String datasetId) {
        public CampaignScenario(String scenarioId) {
            this(scenarioId, null);
        }
    }
}

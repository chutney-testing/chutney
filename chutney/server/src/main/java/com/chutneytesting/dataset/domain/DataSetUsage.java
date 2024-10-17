/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.dataset.domain;

import com.chutneytesting.server.core.domain.dataset.DataSet;
import java.util.List;
import java.util.Set;
import org.springframework.data.util.Pair;

public class DataSetUsage {

    public final DataSet dataSet;
    public final Set<String> campaignUsage;
    public final List<Pair<String, String>> scenarioInCampaignUsage;
    public final Set<String> scenarioUsage;

    protected DataSetUsage(DataSet dataset, Set<String> campaignUsage, List<Pair<String, String>> scenarioInCampaignUsage, Set<String> scenarioUsage) {
        this.dataSet = dataset;
        this.campaignUsage = campaignUsage;
        this.scenarioUsage = scenarioUsage;
        this.scenarioInCampaignUsage = scenarioInCampaignUsage;
    }

    @Override
    public String toString() {
        return "DataSetUsage{" +
            "dataset='" + dataSet.toString() + '\'' +
            ", scenarioUsage=" + scenarioUsage + '\'' +
            ", campaignUsage=" + campaignUsage + '\'' +
            ", scenarioInCampaignUsage=" + scenarioInCampaignUsage + '\'' +
            '}';
    }


    public static DataSetUsageBuilder builder() {
        return new DataSetUsage.DataSetUsageBuilder();
    }

    public static class DataSetUsageBuilder {

        public DataSet dataSet;
        public Set<String> campaignUsage;
        public Set<String> scenarioUsage;
        public List<Pair<String, String>> scenarioInCampaignUsage;

        public DataSetUsageBuilder withScenarioUsage(Set<String> scenarioUsage) {
            this.scenarioUsage = scenarioUsage;
            return this;
        }

        public DataSetUsageBuilder withCampaignUsage(Set<String> campaignUsage) {
            this.campaignUsage = campaignUsage;
            return this;
        }

        public DataSetUsageBuilder withDataset(DataSet dataset) {
            this.dataSet = dataset;
            return this;
        }

        public DataSetUsageBuilder withScenarioInCampaign(List<Pair<String, String>> scenarioInCampaignUsage) {
            this.scenarioInCampaignUsage = scenarioInCampaignUsage;
            return this;
        }

        public DataSetUsage build() {
            return new DataSetUsage(
                this.dataSet,
                this.campaignUsage,
                this.scenarioInCampaignUsage,
                this.scenarioUsage);
        }
    }
}

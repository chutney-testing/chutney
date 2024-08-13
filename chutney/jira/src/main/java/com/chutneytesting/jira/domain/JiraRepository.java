/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.jira.domain;

import java.nio.file.Path;
import java.util.Map;

public interface JiraRepository {

    Path getFolderPath();

    Map<String, String> getAllLinkedCampaigns();

    /**
     * @return key chutney id, value jira id
     */
    Map<String, String> getAllLinkedScenarios();

    Map<String, Map<String, String>> getAllLinkedScenariosWithDataset();

    String getByScenarioId(String scenarioId);

    void saveForScenario(String scenarioId, String jiraId);

    void saveDatasetForScenario(String scenarioId, Map<String, String> datasetLinks);

    void removeForScenario(String scenarioId);

    String getByCampaignId(String campaignId);

    void saveForCampaign(String campaignId, String jiraId);

    void removeForCampaign(String campaignId);

    String getByCampaignExecutionId(String campaignExecutionId);

    void saveForCampaignExecution(String campaignExecutionId, String jiraId);

    void removeForCampaignExecution(String campaignExecutionId);

    JiraServerConfiguration loadServerConfiguration();

    void saveServerConfiguration(JiraServerConfiguration jiraServerConfiguration);
}

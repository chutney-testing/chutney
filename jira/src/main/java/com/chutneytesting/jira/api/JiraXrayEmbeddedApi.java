package com.chutneytesting.jira.api;

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import com.chutneytesting.jira.domain.JiraXrayService;
import com.chutneytesting.jira.xrayapi.XrayTestExecTest;
import java.util.List;

public class JiraXrayEmbeddedApi {

    private final JiraXrayService jiraXrayService;

    public JiraXrayEmbeddedApi(JiraXrayService jiraXrayService) {
        this.jiraXrayService = jiraXrayService;
    }

    public void updateTestExecution(Long campaignId, Long campaignExecutionId, String scenarioId, ReportForJira report) {
        if (report != null && isNotEmpty(scenarioId) && campaignId != null) {
            jiraXrayService.updateTestExecution(campaignId, campaignExecutionId, scenarioId, report);
        }
    }

    public List<XrayTestExecTest> getTestStatusInTestExec(String testExec) { // TODO - Only used in a test ?
        return jiraXrayService.getTestExecutionScenarios(testExec);
    }
}



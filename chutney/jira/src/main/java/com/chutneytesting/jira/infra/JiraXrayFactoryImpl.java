/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.jira.infra;

import com.chutneytesting.jira.domain.JiraServerConfiguration;
import com.chutneytesting.jira.domain.JiraXrayApi;
import com.chutneytesting.jira.domain.JiraXrayClientFactory;

public class JiraXrayFactoryImpl implements JiraXrayClientFactory {

    @Override
    public JiraXrayApi create(JiraServerConfiguration jiraServerConfiguration) {
        return new HttpJiraXrayImpl(jiraServerConfiguration);
    }

}

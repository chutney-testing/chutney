/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.jira.domain;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public record JiraServerConfiguration(
    String url, String username, String password,
    String urlProxy, String userProxy, String passwordProxy
) {

    public boolean isValid() {
        return isNotBlank(url);
    }

    public boolean hasProxy() {
        return isNotBlank(urlProxy);
    }

    public boolean hasProxyWithAuth() {
        return isNotBlank(urlProxy) &&
            isNotBlank(userProxy) &&
            isNotBlank(passwordProxy);
    }
}

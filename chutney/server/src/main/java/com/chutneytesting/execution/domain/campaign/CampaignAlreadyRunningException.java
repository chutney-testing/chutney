/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.domain.campaign;

import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import java.time.format.DateTimeFormatter;

public class CampaignAlreadyRunningException extends RuntimeException {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss");

    public CampaignAlreadyRunningException(CampaignExecution currentReport) {
        super(String.format("Campaign [%s] is already running on [%s] since [%s]",
            currentReport.campaignName,
            currentReport.executionEnvironment,
            currentReport.startDate.format(DATE_TIME_FORMATTER)));
    }
}

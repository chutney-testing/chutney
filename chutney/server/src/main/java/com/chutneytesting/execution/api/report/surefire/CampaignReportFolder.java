/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.api.report.surefire;

import java.util.Set;

/**
 * Represents data needed to create campaign-named folder in surefire reports ZIP.
 */
class CampaignReportFolder {
    final String name;
    final Set<Testsuite> scenariosReport;

    CampaignReportFolder(String name, Set<Testsuite> scenariosReport) {
        this.name = name;
        this.scenariosReport = scenariosReport;
    }
}

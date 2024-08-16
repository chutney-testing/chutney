/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.execution.report;

@SuppressWarnings("serial")
public class ReportNotFoundException extends RuntimeException {

    public ReportNotFoundException(String scenarioId, Long reportId) {
        super("Unable to find report " + reportId + " of scenario " + scenarioId);
    }

    public ReportNotFoundException(Long reportId) {
        super("Unable to find report " + reportId);
    }
}

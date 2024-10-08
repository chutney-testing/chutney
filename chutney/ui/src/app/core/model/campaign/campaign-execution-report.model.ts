/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { ScenarioExecutionReportOutline } from '.';
import { Execution } from '../scenario';
import { ExecutionStatus } from '../scenario/execution-status';
import { Dataset } from "@core/model";

export interface CampaignExecutionReport {
    executionId?: number,
    scenarioExecutionReports: Array<ScenarioExecutionReportOutline>,
    status?: string,
    duration?: string,
    startDate?: string,
    campaignName?: string,
    partialExecution?: boolean,
    executionEnvironment?: string,
    dataset?: Dataset,
    campaignId?: string,
    user: string
}

export interface CampaignExecutionFullReport {
    executionId?: number,
    scenarioExecutionReports: Array<Execution>,
    status?: string,
    duration?: string,
    startDate?: string,
    campaignName?: string,
    partialExecution?: boolean,
    executionEnvironment?: string,
    campaignId?: string,
    user: string
}

export class CampaignReport {
    report: CampaignExecutionReport;

    passed: number;
    running: number;
    failed: number;
    stopped: number;
    notexecuted: number;
    pause: number;
    total: number;

    constructor(report: CampaignExecutionReport) {
        this.report = report;

        const counts = this.initCounts(report);
        this.notexecuted = counts[0];
        this.running = counts[1];
        this.passed = counts[2];
        this.failed = counts[3];
        this.stopped = counts[4];
        this.pause = counts[5];
        this.total = this.passed + this.failed + this.stopped + this.notexecuted + this.running + this.pause;
    }

    private initCounts(report: CampaignExecutionReport): Array<number> {
        var runnings = 0;
        var success = 0;
        var failures = 0;
        var stops = 0;
        var notExecuted = 0;
        var pauses = 0;
        report.scenarioExecutionReports.forEach(r => {
            switch (r.status) {
                case ExecutionStatus.NOT_EXECUTED:
                    notExecuted++;
                    break;
                case ExecutionStatus.RUNNING:
                    runnings++;
                    break;
                case ExecutionStatus.SUCCESS:
                    success++;
                    break;
                case ExecutionStatus.FAILURE:
                    failures++;
                    break;
                case ExecutionStatus.STOPPED:
                    stops++;
                    break;
                case ExecutionStatus.PAUSED:
                    pauses++;
                    break;
            }
        });
        return [notExecuted, runnings, success, failures, stops, pauses];
    }

    allPassed() {
        return this.passed === this.report.scenarioExecutionReports.length;
    }

    hasPassed() {
        return !!this.passed;
    }

    hasFailed() {
        return !!this.failed;
    }

    hasStopped() {
        return !!this.stopped;
    }

    hasNotExecuted() {
        return !!this.notexecuted;
    }

    hasPaused() {
        return !!this.pause;
    }

    hasRunning() {
        return !!this.running;
    }

    isRunning() {
        return ExecutionStatus.RUNNING === this.report.status;
    }

    isPaused() {
        return ExecutionStatus.PAUSED === this.report.status;
    }

    isStopped() {
        return ExecutionStatus.STOPPED === this.report.status;
    }

    refresh(campaignReport: CampaignReport) {
        if (campaignReport.report.campaignId === this.report.campaignId && campaignReport.report.executionId === this.report.executionId) {
            this.report = campaignReport.report;
            this.notexecuted = campaignReport.notexecuted;
            this.running = campaignReport.running;
            this.passed = campaignReport.passed;
            this.failed = campaignReport.failed;
            this.stopped = campaignReport.stopped;
            this.pause = campaignReport.pause;
            this.total = campaignReport.total;
        }
    }
}

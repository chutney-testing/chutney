/**
 * Copyright 2017-2023 Enedis
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

import { Component, Input, OnInit } from "@angular/core";
import { forkJoin, Observable, of, switchMap, timer } from "rxjs";

import { Authorization, CampaignExecutionFullReport, CampaignReport, JiraScenario, JiraTestExecutionScenarios, ScenarioExecutionReport, ScenarioExecutionReportOutline, StepExecutionReport, XrayStatus } from "@core/model";
import { CampaignService, JiraPluginService } from "@core/services";
import { Params, Router } from "@angular/router";
import { ExecutionStatus } from "@core/model/scenario/execution-status";
import { EventManagerService } from "@shared";
import { sortByAndOrder } from '@shared/tools';
import jsPDF from "jspdf";
import autoTable, { CellHookData } from 'jspdf-autotable';
import { DurationPipe } from "@shared/pipes";
import { TranslateService } from "@ngx-translate/core";

@Component({
    selector: 'chutney-campaign-execution',
    templateUrl: './campaign-execution.component.html',
    styleUrls: ['./campaign-execution.component.scss']
})
export class CampaignExecutionComponent implements OnInit {

    @Input() campaignId: number;
    @Input() report: CampaignReport;
    @Input() jiraUrl: string;

    Authorization = Authorization;
    ExecutionStatus = ExecutionStatus;

    jiraTestExecutionId: string;
    private jiraScenarios: JiraScenario[] = [];
    UNSUPPORTED = 'UNSUPPORTED';
    selectedStatusByScenarioId: Map<string, string> = new Map();
    showMore: boolean[] = [];

    orderBy: string;
    reverseOrder: boolean;

    constructor(
        private jiraLinkService: JiraPluginService,
        private campaignService: CampaignService,
        private eventManagerService: EventManagerService,
        private router: Router,
        private translate: TranslateService
    ) { }

    ngOnInit(): void {
        this.cleanJiraUrl();
        forkJoin({
            jirjiraTestExecutionScenarios: this.jiraTestExecutionScenarios$()
        }).subscribe(result => {
            this.jiraScenarios = result.jirjiraTestExecutionScenarios.jiraScenarios;
            this.jiraTestExecutionId = result.jirjiraTestExecutionScenarios.id;
        });
        this.report.report.scenarioExecutionReports.forEach((_report, index) => this.showMore[index] = false);
    }

    private cleanJiraUrl() {
        if (this.jiraUrl && this.jiraUrl.length == 0) {
            this.jiraUrl = null;
        }
    }

    private jiraTestExecutionScenarios$(): Observable<JiraTestExecutionScenarios> {
        return this.jiraLinkService.findByCampaignId(this.campaignId).pipe(
            switchMap((jiraId) => { // TODO - Why this condition ? don't understand it !!
                if (jiraId) {
                    return this.jiraLinkService.findTestExecScenariosByCampaignExecution(this.report.report.executionId);
                } else {
                    return of(new JiraTestExecutionScenarios(null, []));
                }
            })
        );
    }

    xrayStatuses(): Array<string> {
        const keys = Object.keys(XrayStatus);
        return keys.slice();
    }

    selectedUpdateStatus(scenarioId: string, event: any) {
        this.selectedStatusByScenarioId.set(scenarioId, event.target.value);
    }

    updateStatus(scenarioId: string) {
        const newStatus = this.selectedStatusByScenarioId.get(scenarioId);
        if (newStatus === XrayStatus.PASS || newStatus === XrayStatus.FAIL) {
            this.jiraLinkService.updateScenarioStatus(this.jiraTestExecutionId, scenarioId, newStatus).subscribe(
                () => { },
                (error) => {
                    console.log(error);
                }
            );
        }
    }

    scenarioStatus(scenarioId: String): string {
        const jiraScenario = this.jiraScenarios.filter(s => s.chutneyId === scenarioId);
        if (jiraScenario.length > 0) {
            if (jiraScenario[0].executionStatus === XrayStatus.PASS || jiraScenario[0].executionStatus === XrayStatus.FAIL) {
                return jiraScenario[0].executionStatus;
            }
        }
        return this.UNSUPPORTED;
    }

    jiraLinkFrom(chutneyId: string) {
        const foundScenario = this.jiraScenarios.find(s => s.chutneyId === chutneyId);
        if (foundScenario) {
            return this.jiraUrl + '/browse/' + foundScenario.id;
        } else {
            return null;
        }
    }

    toQueryParams(scenarioExecutionReportOutline: ScenarioExecutionReportOutline): Params {
        let execId = scenarioExecutionReportOutline.executionId !== -1 ? scenarioExecutionReportOutline.executionId : 'last';
        return {
            active: execId,
            open: execId,
        }
    }

    replay() {
        this.campaignService.replayFailedScenario(this.report.report.executionId).subscribe({
            error: (error) => this.eventManagerService.broadcast({ name: 'error', msg: error.error })
        });

        timer(1000).pipe(
            switchMap(() => of(this.eventManagerService.broadcast({ name: 'replay', executionId: this.report.report.executionId })))
        ).subscribe();
    }

    stop() {
        this.campaignService.stopExecution(this.campaignId, this.report.report.executionId).subscribe({
            error: (error) => this.eventManagerService.broadcast({ name: 'error', msg: error.error })
        });
    }

    statusClass(scenarioReportOutline: ScenarioExecutionReportOutline): string {
        if (scenarioReportOutline.status === ExecutionStatus.SUCCESS) {
            return 'fa-solid fa-circle-check text-info';
        }
        if (scenarioReportOutline.status === ExecutionStatus.FAILURE) {
            return 'fa-solid fa-circle-xmark text-danger';
        }
        if (scenarioReportOutline.status === ExecutionStatus.RUNNING || scenarioReportOutline.status === ExecutionStatus.PAUSED) {
            return 'fa-solid fa-spinner fa-pulse text-warning';
        }
        if (scenarioReportOutline.status === ExecutionStatus.STOPPED) {
            return 'fa-solid fa-circle-stop text-warning';
        }
        if (scenarioReportOutline.status === ExecutionStatus.NOT_EXECUTED) {
            return 'fa-regular fa-circle text-warning';
        }
        return null;
    }

    sortBy(property) {
        if (this.orderBy === property) {
            this.reverseOrder = !this.reverseOrder;
        }
        this.orderBy = property;

        return sortByAndOrder(
            this.report.report.scenarioExecutionReports,
            (i) => i[property] == null ? '' : i[property],
            this.reverseOrder
        );
    }

    exportReport() {
        this.campaignService.findExecution(this.report.report.executionId)
            .subscribe({
                next: (report: CampaignExecutionFullReport) => {
                    const url = location.origin + '/#' + this.router.serializeUrl(this.router.createUrlTree(['/scenario', ':id', 'executions']));
                    const pdf = new jsPDF('l', 'mm', 'a4');
                    const pdfFontSize = pdf.getFontSize();
                    const duration = new DurationPipe();


                    const docTitle = report.campaignName;
                    pdf.text(docTitle, 148, 20, { align: 'center' });

                    const docRecap = duration.transform(Number.parseInt(this.report.report.duration)) + ' - ' + this.report.passed + ' OK / ' + this.report.total;
                    pdf.setFontSize(pdfFontSize - 4);
                    pdf.text(docRecap, 148, 30, { align: 'center' });

                    const dataHeader = [["id", "Scenario", "Status", "error"]];
                    const dataBody = report.scenarioExecutionReports.map(s => [s.scenarioId.toString(), s.testCaseTitle, s.status, s.error.toString()]);
                    pdf.setFontSize(pdfFontSize - 2);
                    autoTable(pdf, {
                        body: dataBody,
                        head: dataHeader,
                        startY: 40,
                        theme: 'striped',
                        useCss: true,
                        didParseCell(data) {
                            setStatusStyle(data);
                        },
                        didDrawCell(data) {
                            if (data.cell.section === 'body' && data.column.index === 1) {
                                let scenarioId = data.row.cells[0].raw.toString();
                                pdf.link(data.cell.x, data.cell.y, data.cell.width, data.cell.height, { url: url.replace(':id', scenarioId) });
                            }
                        }
                    });

                    pdf.addPage();
                    this.translate.get('campaigns.execution.scenarios.title').subscribe((res: string) => {
                        pdf.text(res, 148, 15, { align: "center" });
                    });

                    const scenarioReportHeader = [["step", "Status", "error"]];

                    report.scenarioExecutionReports
                        .map(s => this.buildExecutionReport(s))
                        .forEach(r => {
                            pdf.text(r.scenarioName, 15, 25);
                            const scenarioReportBody = r.report.steps.map(step => [step.name, step.status, this.buildErrorMessage(step)]);
                            autoTable(pdf, {
                                body: scenarioReportBody,
                                head: scenarioReportHeader,
                                startY: 30,
                                theme: 'striped',
                                useCss: true,
                                didParseCell(data) {
                                    setStatusStyle(data);
                                }
                            });
                            pdf.addPage();
                        });

                    pdf.deletePage(pdf.internal.pages.length - 1);
                    pdf.save('campaignExecutionReport.pdf');
                },
                error: error => {
                    console.error(error.message);
                }
            });
    }

    private buildErrorMessage(step: StepExecutionReport): string {
        if (step.status === 'FAILURE') {
            const s = this.getFailedStep(step);
            return '[ ' + s.name + ' ] ' + s.errors.toString();
        }
        return '';
    }

    private getFailedStep(step: StepExecutionReport): StepExecutionReport {
        return (step.steps && step.steps.length) ? this.getFailedStep(step.steps.filter(s => s.status === 'FAILURE')[0]) : step;
    }

    private buildExecutionReport(jsonResponse: any): ScenarioExecutionReport {
        let report: StepExecutionReport;
        let contextVariables: Map<string, Object>;
        if (jsonResponse?.report) {
            report = JSON.parse(jsonResponse.report).report;
            contextVariables = JSON.parse(jsonResponse.report).contextVariables;
        }
        return new ScenarioExecutionReport(
            jsonResponse.executionId,
            jsonResponse.status ? jsonResponse.status : report?.status,
            jsonResponse.duration ? jsonResponse.duration : report?.duration,
            new Date(jsonResponse.time ? jsonResponse.time : report?.startDate),
            report,
            jsonResponse.environment,
            jsonResponse.user,
            jsonResponse.testCaseTitle,
            jsonResponse.error,
            contextVariables
        );
    }
}
function setStatusStyle(data: CellHookData) {
    if (data.cell.section === 'body') {
        if (data.cell.raw === "FAILURE") {
            data.cell.styles.textColor = [255, 255, 255];
            data.cell.styles.fillColor = '#e74c3c';
        }
        if (data.cell.raw === "SUCCESS") {
            data.cell.styles.textColor = [255, 255, 255];
            data.cell.styles.fillColor = '#18bc9c';
        }
    }
}


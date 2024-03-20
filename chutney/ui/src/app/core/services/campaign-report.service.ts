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

import { Injectable } from "@angular/core";
import { CampaignExecutionFullReport, ScenarioExecutionReport, StepExecutionReport } from "@core/model";
import { ExecutionStatus } from "@core/model/scenario/execution-status";
import { TranslateService } from "@ngx-translate/core";
import { DurationPipe } from "@shared/pipes";
import jsPDF from "jspdf";
import autoTable, { CellHookData } from "jspdf-autotable";

@Injectable({
    providedIn: 'root'
})
export class CampaignReportService {

    constructor(private translate: TranslateService) {
    }

    public toPDF(report: CampaignExecutionFullReport): jsPDF {

        const pdf = new jsPDF('landscape');

        this.campaignSummaryGeneration(pdf, report);
        pdf.addPage();
        this.scenariiSummaryGeneration(pdf, report);

        return pdf;
    }

    private campaignSummaryGeneration(pdf: jsPDF, report: CampaignExecutionFullReport) {
        const url = location.origin + '/#/scenario/:id/executions??open=:executionId&active=:executionId';
        const duration = new DurationPipe();
        const pdfFontSize = pdf.getFontSize();
        const docTitle = report.campaignName;
        pdf.text(docTitle, 148, 20, { align: 'center' });

        const passedScenarioExecutionCount = report.scenarioExecutionReports.filter(s => s.status === ExecutionStatus.SUCCESS).length;
        const totalscenarioExecutionCount = report.scenarioExecutionReports.length;
        const docRecap = duration.transform(Number.parseInt(report.duration)) + ' - ' + passedScenarioExecutionCount + ' OK / ' + totalscenarioExecutionCount;
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
                CampaignReportService.setStatusStyle(data);
            },
            didDrawCell(data) {
                if (data.cell.section === 'body' && data.column.index === 1) {
                    const scenarioId = data.row.cells[0].raw.toString();
                    const executionId = report.scenarioExecutionReports.filter(s => s.scenarioId === scenarioId).map(s => s.executionId).shift().toString();
                    pdf.link(data.cell.x, data.cell.y, data.cell.width, data.cell.height, { url: url.replace(':id', scenarioId).replace(new RegExp(':executionId', 'g'), executionId) });
                }
            }
        });
    }

    private scenariiSummaryGeneration(pdf: jsPDF, report: CampaignExecutionFullReport) {
        const scenariiDetailsTitle = this.translate.instant('campaigns.execution.scenarios.title');
        pdf.text(scenariiDetailsTitle, 148, 15, { align: "center" });

        const scenarioReportHeader = [["step", "Status", "error"]];

        report.scenarioExecutionReports
            .map(s => this.buildExecutionReport(s))
            .forEach((r, index) => {
                if (index){
                    pdf.addPage();
                }
                pdf.text(r.scenarioName, 15, 25);
                const scenarioReportBody = r.report.steps.map(step => [step.name, step.status, this.buildErrorMessage(step)]);
                autoTable(pdf, {
                    body: scenarioReportBody,
                    head: scenarioReportHeader,
                    startY: 30,
                    theme: 'striped',
                    useCss: true,
                    didParseCell(data) {
                        CampaignReportService.setStatusStyle(data);
                    }
                });
            });
    }

    private buildErrorMessage(step: StepExecutionReport): string {
        if (step.status === ExecutionStatus.FAILURE) {
            const s = this.getFailedStep(step);
            return '[ ' + s.name + ' ] ' + s.errors.toString();
        }
        return '';
    }

    private getFailedStep(step: StepExecutionReport): StepExecutionReport {
        return (step.steps && step.steps.length) ? this.getFailedStep(step.steps.filter(s => s.status === ExecutionStatus.FAILURE)[0]) : step;
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

    private static setStatusStyle(data: CellHookData) {
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
}
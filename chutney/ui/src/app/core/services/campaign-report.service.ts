/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Injectable } from '@angular/core';
import { CampaignExecutionFullReport, KeyValue, ScenarioExecutionReport, StepExecutionReport } from '@core/model';
import { ExecutionStatus } from '@core/model/scenario/execution-status';
import { TranslateService } from '@ngx-translate/core';
import { DurationPipe } from '@shared/pipes';
import jsPDF from 'jspdf';
import autoTable, { CellHookData } from 'jspdf-autotable';
import { ExecutionDataset } from "@core/model/scenario/execution.dataset";

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
        this.scenarioSummaryGeneration(pdf, report);

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

        let dataHeader, dataBody;
        const hasDataset = report.scenarioExecutionReports.some(s => s.dataset);
        if(hasDataset){
            dataHeader = [["id", "Scenario", "Status", "Dataset", "error"]];
            dataBody = report.scenarioExecutionReports.map(s => [s.scenarioId.toString(), s.testCaseTitle, s.status, s.dataset, s.error.toString()]);
        } else {
            dataHeader = [["id", "Scenario", "Status", "error"]];
            dataBody = report.scenarioExecutionReports.map(s => [s.scenarioId.toString(), s.testCaseTitle, s.status, s.error.toString()]);
        }

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

    private scenarioSummaryGeneration(pdf: jsPDF, report: CampaignExecutionFullReport) {
        const scenariiDetailsTitle = this.translate.instant('campaigns.execution.scenarios.title');
        pdf.text(scenariiDetailsTitle, 148, 15, { align: "center" });

        const scenarioReportHeader = [["step", "Status", "error"]];

        report.scenarioExecutionReports
            .forEach((s, index) => {
                let startY = 30;
                if (index){
                    pdf.addPage();
                }
                let r = this.buildExecutionReport(s);
                pdf.text(r.scenarioName, 15, 25);
                if(s.dataset) {
                    pdf.text(`${this.translate.instant('scenarios.execution.dataset.title')}: ${s.dataset}`, 15, startY);
                    startY += 5;
                }
                const scenarioReportBody = r.report.steps.map(step => [step.name, step.status, this.buildErrorMessage(step)]);
                autoTable(pdf, {
                    body: scenarioReportBody,
                    head: scenarioReportHeader,
                    startY: startY,
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
        let datasetVariables: Map<string, Object>;
        if (jsonResponse?.report) {
            let parse = JSON.parse(jsonResponse.report);
            report = parse.report;
            contextVariables = parse.contextVariables;
            datasetVariables = parse.datasetVariables;
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
            contextVariables,
            datasetVariables ? new ExecutionDataset(
                datasetVariables.get("constants") as Array<KeyValue>,
                datasetVariables.get("datatable") as Array<Array<KeyValue>>,
                String(datasetVariables.get("datasetId"))
            ) : null
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

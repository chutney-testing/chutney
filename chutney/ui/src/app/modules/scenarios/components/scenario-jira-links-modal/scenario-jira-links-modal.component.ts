/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import {Component, inject, Input, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {JiraDatasetLinks, JiraScenarioLinks, ScenarioIndex} from '@core/model';
import {JiraPluginService} from '@core/services';
import {NgbActiveModal} from '@ng-bootstrap/ng-bootstrap';


@Component({
    selector: 'scenarioJiraLinks-modal',
    templateUrl: './scenario-jira-links-modal.component.html',
    styleUrls: ['./scenario-jira-links-modal.component.scss']
})
export class ScenarioJiraLinksModalComponent implements OnInit {

    activeModal = inject(NgbActiveModal);

    @Input() scenario: ScenarioIndex;
    @Input() jiraUrl: string;
    isEditable: boolean = false;

    jiraDatasetList: Array<JiraDatasetLinks> = new Array();

    datasetForm: FormArray;
    jiraFormGroup: FormGroup;

    errorMessage = "";

    constructor(
        private jiraPluginService: JiraPluginService,
        private formBuilder: FormBuilder
    ) {
        this.datasetForm = this.formBuilder.array([]);
        this.jiraFormGroup = this.formBuilder.group({
            jiraId: '',
        });
    }


    ngOnInit(): void {
        this.loadJiraLinks();
    }

    edit() {
        this.isEditable = true;
        if( !this.jiraDatasetList.length ){
            this.newEntry();
        }
    }

    save() {
        let datasetLinks = this.getDatasetLinks();
        let jiraId = this.jiraFormGroup.controls["jiraId"].value;
        this.jiraPluginService.saveForScenario(new JiraScenarioLinks(jiraId,this.scenario.id,datasetLinks)).subscribe({
            error: (error) => {
                this.errorMessage = error.message;
                return;
            },
            complete: () => {
                this.activeModal.close();
            }
        });
    }

    newEntry() {
        const index = this.datasetForm.controls.length;
        this.datasetForm.insert(index,this.createNewEntry("",""));
     }

    deleteEntry(index: number) {
        this.datasetForm.removeAt(index);
    }

    selectDataset(dataset: string,index: number) {
        this.datasetForm["controls"][index].get("datasetId").setValue(dataset);
    }

    getJiraLink(id: string) {
        return this.jiraUrl + '/browse/' + id;
    }

    private loadJiraLinks() {
        this.jiraPluginService.findByScenarioId(this.scenario.id).subscribe({
            next: (res) => {
                this.jiraDatasetList = Object.entries(res.datasetLinks).map(e => new JiraDatasetLinks(e[0],e[1]));
                this.jiraFormGroup.controls["jiraId"].setValue(res.id);
                for (let i = 0; i < this.jiraDatasetList.length; i++) {
                    this.datasetForm.insert(
                        i,
                        this.createNewEntry(
                            this.jiraDatasetList[i]["dataset"],
                            this.jiraDatasetList[i]["jiraId"]
                        )
                    );
                }
            },
            error: (error) => {
                this.errorMessage = error.message;
            }
        });
    }

    private createNewEntry(key?: string, value?: string): FormGroup {
        return this.formBuilder.group({
            datasetId: key ? key : '',
            jiraId: value ? value : ''
        });
    }

    private getDatasetLinks(): any {
        let datasetLinks = {};
        this.datasetForm.value.forEach(e => {
            if( e.datasetId && e.jiraId ){
                datasetLinks[e.datasetId] = e.jiraId;
            }
        });

        return datasetLinks;
    }
}

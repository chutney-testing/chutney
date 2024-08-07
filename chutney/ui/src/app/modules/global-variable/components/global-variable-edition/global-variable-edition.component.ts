/**
 * Copyright 2017-2024 Enedis
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

import {Component, OnInit, ViewChild} from '@angular/core';
import {GlobalVariableService} from '@core/services/global-var.service';
import {HttpErrorResponse} from '@angular/common/http';

import { Authorization } from '@model';

@Component({
    selector: 'chutney-global-variable-edition',
    templateUrl: './global-variable-edition.component.html',
    styleUrls: ['./global-variable-edition.component.scss']
})
export class GlobalVariableEditionComponent implements OnInit {

    data = '';
    modifiedContent;
    fileNames;
    currentFileName;
    message: string;

    help = false;

    Authorization = Authorization;

    constructor(private globalVariableService: GlobalVariableService) {
    }

    ngOnInit(): void {
        this.globalVariableService.list().subscribe(
            response => {
                this.fileNames = response;
                this.currentFileName = this.fileNames[0];
                this.updateFileContent(this.currentFileName);
            }
        );
    }

    callBackFunc(data) {
        this.modifiedContent = data;
    }

    save() {
        (async () => {
            this.message = 'Saving...';
            await this.delay(1000);
            this.globalVariableService.save(this.currentFileName, this.modifiedContent).subscribe(
                res => {
                    this.message = 'Document saved';
                    if (this.fileNames.indexOf(this.currentFileName) === -1) {
                        this.fileNames.push(this.currentFileName);
                    }
                },
                error => this.handleError(error));
        })();
    }

    delay(ms: number) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    private handleError(err: HttpErrorResponse) {
        if (err.error instanceof ProgressEvent) {
            this.message = 'Back-end server not reachable';
        } else {
            this.message = err.error;
        }
    }

    updateFileContent(selectedFileName: string) {
        if (selectedFileName === undefined) {
            this.data = '';
            this.modifiedContent = '';
        } else {
            this.globalVariableService.get(selectedFileName).subscribe(
                response => {
                    this.data = response;
                    this.modifiedContent = response;
                }
            );
        }
    }

    deleteFile() {
        (async () => {
            this.message = 'Deleting...';
            await this.delay(1000);
            this.globalVariableService.delete(this.currentFileName).subscribe(
                res => {
                    this.fileNames.splice(this.fileNames.indexOf(this.currentFileName), 1);
                    this.currentFileName = this.fileNames[0];
                    this.message = 'Document deleted';
                    this.updateFileContent(this.currentFileName);
                },
                error => this.handleError(error));
        })();
    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, EventEmitter, Input, OnChanges, Output } from '@angular/core';

@Component({
    selector: 'chutney-environment-combo',
    templateUrl: './environment-combo.component.html',
    styleUrls: ['./environment-combo.component.scss']
})
export class EnvironmentComboComponent implements OnChanges {

    @Input() environments: Array<string>;
    @Input() defaultValue: string;
    @Output() selectionEvent = new EventEmitter();

    selectedEnvironment: string | null;

    constructor() {
    }

    ngOnChanges() {
        this.selectedEnvironment = null
        this.setSelectedEnvironment();
    }

    changingValue(event: any) {
        this.selectedEnvironment = this.environments.filter(env => env === event.target.value)[0];
        this.selectionEvent.emit(this.selectedEnvironment);
    }

    private setSelectedEnvironment() {
        if (this.environments && this.environments.length > 0) {
            const envFound = this.environments.find(e => e === this.defaultValue);
            if (envFound) {
                this.selectedEnvironment = envFound;
            }
            this.selectionEvent.emit(this.selectedEnvironment);
        }
    }
}

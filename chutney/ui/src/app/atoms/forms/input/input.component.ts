/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
selector: 'chutney-forms-input',
template: `
    <input
        id="{{id}}"
        name="{{id}}"
        type="{{type}}"
        placeholder="{{placeholder}}"
        [ngModel]="model"
        (ngModelChange)="onInputChange($event)"
        class="form-control"
        [ngClass]="{'invalid': !validate(model)}"
    />
`,
styleUrls: ['./input.component.scss']
})
export class InputComponent {

    @Input() id: string;
    @Input() placeholder: string;
    @Input() type = 'text';
    @Input() model: string;
    @Output() modelChange = new EventEmitter<string>();
    @Input() validate: (value: string) => boolean = (_) => true;

    constructor() { }

    onInputChange(newValue: string) {
        this.model = newValue;
        this.modelChange.emit(this.model);
    }
}

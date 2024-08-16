/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, Input } from '@angular/core';

@Component({
selector: 'chutney-button',
template: `
    <button class="{{level}}" [disabled]="disabled">
      @if (iconClass) {
        <span class="fa {{iconClass}}"></span>
      }
      {{model}}
    </button>
    `,
styleUrls: ['./button.component.scss']
})
export class ButtonComponent {

    @Input() model: string;
    @Input() iconClass: string;
    @Input() level = 'first';
    @Input() disabled = false;

    constructor() { }
}

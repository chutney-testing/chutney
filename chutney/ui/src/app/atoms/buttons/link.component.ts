/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, Input } from '@angular/core';

@Component({
selector: 'chutney-link',
template: `
    <a class="chutney-link" >
        {{model}}
    </a>
`,
styleUrls: ['./link.component.scss']
})
export class LinkComponent {

    @Input() model: string;

    constructor() { }
}

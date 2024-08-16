/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { LinkifierService } from '@core/services';
import { LayoutOptions } from '@core/layout/layout-options.service';

@Component({
    selector: 'chutney-parent',
    templateUrl: './parent.component.html',
    styleUrls: ['./parent.component.scss']
})
export class ParentComponent implements OnInit, OnDestroy {

    private linkifierSubscription: Subscription;

    constructor(public layoutOptions: LayoutOptions,
                private linkifierService: LinkifierService) {
        this.linkifierSubscription = this.linkifierService.loadLinkifiers().subscribe();

    }

    ngOnInit(): void {
    }

    ngOnDestroy() {
        if (this.linkifierSubscription) {
            this.linkifierSubscription.unsubscribe();
        }
    }

}

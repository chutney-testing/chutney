/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { IDropdownSettings } from 'ng-multiselect-dropdown';
import { TranslateService } from '@ngx-translate/core';
import { Injectable, InjectionToken } from '@angular/core';

export const DROPDOWN_SETTINGS = new InjectionToken<DropdownSettings>('DropdownSettings');

@Injectable()
export class DropdownSettings implements IDropdownSettings {
    searchPlaceholderText: string;
    noDataAvailablePlaceholderText: string;
    enableCheckAll= false;
    allowSearchFilter= true;

    constructor(private translateService: TranslateService) {
        this.searchPlaceholderText = this.translateService.instant('global.actions.search');
        this.noDataAvailablePlaceholderText = this.translateService.instant('global.msg.empty');
    }

}

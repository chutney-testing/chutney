/*
 *  Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

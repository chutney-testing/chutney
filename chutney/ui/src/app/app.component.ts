/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';
import { registerLocaleData } from '@angular/common';
import localeFr from '@angular/common/locales/fr';
import * as moment from 'moment';

@Component({
    selector: 'chutney-main',
    templateUrl: './app.component.html',
    styleUrl: './app.component.scss'
})
export class AppComponent {


    constructor(private translate: TranslateService) {
        // this language will be used as a fallback when a translation isn't found in the current language
        translate.setDefaultLang('en');
        // // the lang to use, if the lang isn't available, it will use the current loader to get them
        // // take only language designator, i.e. forget about region
        let lang = navigator.language.substring(0, 2) || translate.getDefaultLang();
        translate.use(lang);
        registerLocaleData(localeFr);
        this.updateMomentLocal(lang);
    }

    private updateMomentLocal(lang: string) {
        moment.updateLocale(lang, chutneyMomentCalendar[lang]);
    }
}

const chutneyMomentCalendar = {
    'en': {
        calendar: {
            sameDay: '[Today at] HH:mm',
            nextDay: '[Tomorrow at] HH:mm',
            nextWeek: 'dddd [at] HH:mm',
            lastDay: '[Yesterday at] HH:mm',
            lastWeek: '[Last] dddd [at] HH:mm',
            sameElse: 'L [at] HH:mm',
        }
    },
    'fr': {
        calendar: {
            sameDay: '[Aujourd’hui à] HH:mm',
            nextDay: '[Demain à] HH:mm',
            nextWeek: 'dddd [à] HH:mm',
            lastDay: '[Hier à] HH:mm',
            lastWeek: 'dddd [dernier à] HH:mm',
            sameElse: 'L [à] HH:mm',
        }
    }
};

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { inject } from '@angular/core';
import { CanDeactivateFn } from '@angular/router';
import { CanDeactivatePage } from './page';
import { TranslateService } from '@ngx-translate/core';

export const canDeactivateGuard : CanDeactivateFn<CanDeactivatePage> = (page: CanDeactivatePage) => {
    if (page && page.canDeactivatePage && !page.canDeactivatePage()) {
        const translationService = inject(TranslateService);
        return confirm(translationService.instant('global.confirm.page.deactivate'));
    }
    return true;
}

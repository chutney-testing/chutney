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

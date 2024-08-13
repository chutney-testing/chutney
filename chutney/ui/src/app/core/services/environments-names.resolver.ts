/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { EnvironmentService } from '@core/services';


export const environmentsNamesResolver: ResolveFn<string[]> =
    () => inject(EnvironmentService).names();

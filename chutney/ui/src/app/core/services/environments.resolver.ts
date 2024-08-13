/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { inject } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { EnvironmentService } from '@core/services';
import { Environment } from '@model';


export const environmentsResolver: ResolveFn<Environment[]> = () => inject(EnvironmentService).list();

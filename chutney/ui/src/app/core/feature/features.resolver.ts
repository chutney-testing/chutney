/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { inject } from '@angular/core';
import { Feature } from '@core/feature/feature.model';
import { FeatureService } from '@core/feature/feature.service';
import { ResolveFn } from '@angular/router';


export const featuresResolver: ResolveFn<Feature[]> = () => inject(FeatureService).loadFeatures();

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Authorization } from '@model';
import { FeatureName } from '@core/feature/feature.model';

export class MenuItem {
    label: string;
    link?: string;
    click?: Function;
    iconClass?: string;
    secondaryIconClass?: string;
    authorizations?: Authorization[];
    feature?: FeatureName;
    options?: { id: string, label: string }[];
    children?: MenuItem[] = [];
    disabled?: boolean = false;
}

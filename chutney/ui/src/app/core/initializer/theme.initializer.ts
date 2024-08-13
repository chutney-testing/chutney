/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { ThemeService } from '@core/theme/theme.service';


export function themeInitializer(themeService: ThemeService): () => void {
    return () => themeService.applyCurrentTheme();
}

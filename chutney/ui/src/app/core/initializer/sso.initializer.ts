/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { SsoService } from "@core/services/sso.service";

export function ssoInitializer(ssoOpenIdConnectService: SsoService): () => void {
    return () => ssoOpenIdConnectService.fetchSsoConfig()
}

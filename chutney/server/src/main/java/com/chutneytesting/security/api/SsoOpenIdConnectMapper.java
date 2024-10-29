/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.api;

import com.chutneytesting.security.infra.sso.SsoOpenIdConnectConfigProperties;

public class SsoOpenIdConnectMapper {
    public static SsoOpenIdConnectConfigDto toDto(SsoOpenIdConnectConfigProperties ssoOpenIdConnectConfig) {
        if (ssoOpenIdConnectConfig == null) {
            return null;
        }
        return new SsoOpenIdConnectConfigDto(
            ssoOpenIdConnectConfig.issuer,
            ssoOpenIdConnectConfig.clientId,
            ssoOpenIdConnectConfig.clientSecret,
            ssoOpenIdConnectConfig.responseType,
            ssoOpenIdConnectConfig.scope,
            ssoOpenIdConnectConfig.redirectBaseUrl,
            ssoOpenIdConnectConfig.ssoProviderName,
            ssoOpenIdConnectConfig.oidc
        );
    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.api;

import static java.util.Optional.ofNullable;

import com.chutneytesting.security.infra.sso.SsoOpenIdConnectConfigProperties;

public class SsoOpenIdConnectMapper {
    public static SsoOpenIdConnectConfigDto toDto(SsoOpenIdConnectConfigProperties ssoOpenIdConnectConfig) {
        return ofNullable(ssoOpenIdConnectConfig)
            .map(config -> new SsoOpenIdConnectConfigDto(
                ssoOpenIdConnectConfig.issuer,
                ssoOpenIdConnectConfig.clientId,
                ssoOpenIdConnectConfig.clientSecret,
                ssoOpenIdConnectConfig.responseType,
                ssoOpenIdConnectConfig.scope,
                ssoOpenIdConnectConfig.redirectBaseUrl,
                ssoOpenIdConnectConfig.ssoProviderName,
                ssoOpenIdConnectConfig.oidc,
                ssoOpenIdConnectConfig.uriRequireHeader,
                ssoOpenIdConnectConfig.headers,
                ssoOpenIdConnectConfig.ssoProviderImageUrl,
                ssoOpenIdConnectConfig.additionalQueryParams
            )).orElse(null);
    }
}

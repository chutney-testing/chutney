/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.domain;

import com.chutneytesting.security.infra.sso.SsoOpenIdConnectConfigProperties;

public class SsoOpenIdConnectMapper {
    public static SsoOpenIdConnectConfig toDomain(SsoOpenIdConnectConfigProperties ssoOpenIdConnectConfigProperties) {
        if (ssoOpenIdConnectConfigProperties == null) {
            return null;
        }
        return new SsoOpenIdConnectConfig(
            ssoOpenIdConnectConfigProperties.issuer,
            ssoOpenIdConnectConfigProperties.clientId,
            ssoOpenIdConnectConfigProperties.clientSecret,
            ssoOpenIdConnectConfigProperties.responseType,
            ssoOpenIdConnectConfigProperties.scope,
            ssoOpenIdConnectConfigProperties.redirectBaseUrl,
            ssoOpenIdConnectConfigProperties.ssoProviderName,
            ssoOpenIdConnectConfigProperties.oidc
        );
    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.domain;

import static com.chutneytesting.security.domain.SsoOpenIdConnectMapper.toDomain;

import com.chutneytesting.security.infra.sso.SsoOpenIdConnectConfigProperties;

public class SsoOpenIdConnectConfigService {

    private final SsoOpenIdConnectConfigProperties ssoOpenIdConnectConfigProperties;

    public SsoOpenIdConnectConfigService(SsoOpenIdConnectConfigProperties ssoOpenIdConnectConfigProperties) {
        this.ssoOpenIdConnectConfigProperties = ssoOpenIdConnectConfigProperties;
    }

    public SsoOpenIdConnectConfig getSsoOpenIdConnectConfig() {
        return toDomain(ssoOpenIdConnectConfigProperties);
    }
}

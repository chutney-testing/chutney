/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.infra.sso;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("auth.sso")
public class SsoOpenIdConnectConfigProperties implements InitializingBean  {

    public final String issuer;
    public final String clientId;
    public final String clientSecret;
    public final String responseType;
    public final String scope;
    public final String redirectBaseUrl;
    public final String ssoProviderName;
    public final Boolean oidc;

    public SsoOpenIdConnectConfigProperties(String issuer, String clientId, String clientSecret, String responseType, String scope, String redirectBaseUrl, String ssoProviderName, Boolean oidc) {
        this.issuer = issuer;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.responseType = responseType;
        this.scope = scope;
        this.redirectBaseUrl = redirectBaseUrl;
        this.ssoProviderName = ssoProviderName;
        this.oidc = oidc;
    }

    @Override
    public void afterPropertiesSet() {}
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.api;

import java.util.Map;

public class SsoOpenIdConnectConfigDto {
    public final String issuer;
    public final String clientId;
    public final String clientSecret;
    public final String responseType;
    public final String scope;
    public final String redirectBaseUrl;
    public final String ssoProviderName;
    public final Boolean oidc;
    public final String uriRequireHeader;
    public final Map<String, String> headers;
    public final String ssoProviderImageUrl;
    public final Map<String, String> additionalQueryParams;


    public SsoOpenIdConnectConfigDto(String issuer, String clientId, String clientSecret, String responseType, String scope, String redirectBaseUrl, String ssoProviderName, Boolean oidc, String uriRequireHeader, Map<String, String> headers, String ssoProviderImageUrl, Map<String, String> additionalQueryParams) {
        this.issuer = issuer;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.responseType = responseType;
        this.scope = scope;
        this.redirectBaseUrl = redirectBaseUrl;
        this.ssoProviderName = ssoProviderName;
        this.oidc = oidc;
        this.uriRequireHeader = uriRequireHeader;
        this.headers = headers;
        this.ssoProviderImageUrl = ssoProviderImageUrl;
        this.additionalQueryParams = additionalQueryParams;
    }
}

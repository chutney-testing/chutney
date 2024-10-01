/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SsoOpenIdConnectController.BASE_URL)
@CrossOrigin(origins = "*")
public class SsoOpenIdConnectController {

    public static final String BASE_URL = "/api/v1/sso";

    private final SsoOpenIdConnectConfig ssoOpenIdConnectConfig;

    SsoOpenIdConnectController(@Value("${auth.sso.issuer}") String issuer,
                               @Value("${auth.sso.clientId}") String clientId,
                               @Value("${auth.sso.responseType}") String responseType,
                               @Value("${auth.sso.scope}") String scope,
                               @Value("${auth.sso.redirectBaseUrl}") String redirectUri,
                               @Value("${auth.sso.ssoProviderName}") String ssoProviderName) {
        this.ssoOpenIdConnectConfig = new SsoOpenIdConnectConfig(issuer, clientId, responseType, scope, redirectUri, ssoProviderName);
    }

    @GetMapping(path = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
    public SsoOpenIdConnectConfig getLastCampaignExecution() {
        return ssoOpenIdConnectConfig;
    }
}

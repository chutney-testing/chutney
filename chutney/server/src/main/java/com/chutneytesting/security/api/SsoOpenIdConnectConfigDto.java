/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.api;

import java.util.Map;

public record SsoOpenIdConnectConfigDto(String issuer, String clientId, String clientSecret, String responseType,
                                        String scope, String redirectBaseUrl, String ssoProviderName, Boolean oidc,
                                        String uriRequireHeader, Map<String, String> headers,
                                        String ssoProviderImageUrl, Map<String, String> additionalQueryParams) {
}

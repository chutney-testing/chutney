/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.api;

public record SsoOpenIdConnectConfig(String issuer, String clientId, String responseType, String scope, String redirectBaseUrl, String ssoProviderName) {

}

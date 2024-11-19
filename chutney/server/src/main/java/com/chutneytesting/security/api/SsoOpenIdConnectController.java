/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.api;

import static com.chutneytesting.security.api.SsoOpenIdConnectMapper.toDto;

import com.chutneytesting.security.infra.sso.SsoOpenIdConnectConfigProperties;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SsoOpenIdConnectController.BASE_URL)
@CrossOrigin(origins = "*")
public class SsoOpenIdConnectController {

    public static final String BASE_URL = "/api/v1/sso";

    private final SsoOpenIdConnectConfigProperties ssoOpenIdConnectConfigProperties;

    SsoOpenIdConnectController(@Nullable SsoOpenIdConnectConfigProperties ssoOpenIdConnectConfigProperties) {
        this.ssoOpenIdConnectConfigProperties = ssoOpenIdConnectConfigProperties;
    }

    @GetMapping(path = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
    public SsoOpenIdConnectConfigDto getSsoOpenIdConnectConfig() {
        return toDto(ssoOpenIdConnectConfigProperties);
    }
}

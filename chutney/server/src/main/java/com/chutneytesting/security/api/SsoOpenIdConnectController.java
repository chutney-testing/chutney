/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.api;

import static com.chutneytesting.security.api.SsoOpenIdConnectMapper.toDto;

import com.chutneytesting.security.domain.SsoOpenIdConnectConfigService;
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

    private final SsoOpenIdConnectConfigService ssoOpenIdConnectConfigService;

    SsoOpenIdConnectController(SsoOpenIdConnectConfigService ssoOpenIdConnectConfigService) {
        this.ssoOpenIdConnectConfigService = ssoOpenIdConnectConfigService;
    }

    @GetMapping(path = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
    public SsoOpenIdConnectConfigDto getSsoOpenIdConnectConfig() {
        if (ssoOpenIdConnectConfigService == null) {
            return null;
        }
        return toDto(ssoOpenIdConnectConfigService.getSsoOpenIdConnectConfig());
    }
}

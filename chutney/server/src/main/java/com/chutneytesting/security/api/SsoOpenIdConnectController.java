/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.api;

import static com.chutneytesting.security.api.SsoOpenIdConnectMapper.toDto;
import static java.util.Optional.ofNullable;

import com.chutneytesting.security.infra.sso.SsoOpenIdConnectConfigProperties;
import java.util.NoSuchElementException;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(SsoOpenIdConnectController.BASE_URL)
@CrossOrigin(origins = "*")
@Profile("sso-auth")
public class SsoOpenIdConnectController {

    public static final String BASE_URL = "/api/v1/sso";

    private final SsoOpenIdConnectConfigProperties ssoOpenIdConnectConfigProperties;

    SsoOpenIdConnectController(SsoOpenIdConnectConfigProperties ssoOpenIdConnectConfigProperties) {
        this.ssoOpenIdConnectConfigProperties = ssoOpenIdConnectConfigProperties;
    }

    @GetMapping(path = "/config", produces = MediaType.APPLICATION_JSON_VALUE)
    public SsoOpenIdConnectConfigDto getSsoOpenIdConnectConfig() {
        return ofNullable(toDto(ssoOpenIdConnectConfigProperties))
            .orElseThrow(NoSuchElementException::new);
    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.infra.sso;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class OAuth2TokenAuthenticationProvider implements AuthenticationProvider {

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
    private final ClientRegistration clientRegistration;

    public OAuth2TokenAuthenticationProvider(OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService, ClientRegistration clientRegistration) {
        this.oAuth2UserService = oAuth2UserService;
        this.clientRegistration = clientRegistration;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        OAuth2AuthenticationToken tokenAuth = (OAuth2AuthenticationToken) authentication;
        String token = tokenAuth.getCredentials().toString();
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, token, null, null);
        OAuth2UserRequest userRequest = new OAuth2UserRequest(clientRegistration, accessToken);
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        if (oAuth2User != null) {
            return new UsernamePasswordAuthenticationToken(oAuth2User.getAttribute("user"), null, oAuth2User.getAuthorities());
        } else {
            throw new BadCredentialsException("Invalid token");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return OAuth2AuthenticationToken.class.isAssignableFrom(authentication);
    }
}

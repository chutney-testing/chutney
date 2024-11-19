/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.infra.sso;

import com.chutneytesting.security.api.UserDto;
import com.chutneytesting.security.domain.AuthenticationService;
import com.chutneytesting.security.infra.UserDetailsServiceHelper;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestOperations;

public class OAuth2SsoUserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final AuthenticationService authenticationService;
    private final RestOperations restOperations;

    public OAuth2SsoUserService(AuthenticationService authenticationService, @Nullable RestOperations restOperations) {
        this.authenticationService = authenticationService;
        this.restOperations = restOperations;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        if (restOperations != null) {
            delegate.setRestOperations(restOperations);
        }
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        Map<String, Object> oAuth2UserAttributes = oAuth2User.getAttributes();
        String username = (String) oAuth2UserAttributes.get("sub");
        UserDto user = new UserDto();
        user.setId(username);
        user.setName(username);
        user.setRoles(Collections.emptySet());
        user = UserDetailsServiceHelper.grantAuthoritiesFromUserRole(user, authenticationService);
        Map<String, Object> attributes = new HashMap<>(oAuth2UserAttributes);
        attributes.put("user", user);
        return new DefaultOAuth2User(user.getAuthorities(), attributes, "sub");
    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.infra.sso;

import static java.util.stream.Collectors.toUnmodifiableMap;

import com.chutneytesting.security.api.UserDto;
import com.chutneytesting.security.domain.AuthenticationService;
import com.chutneytesting.security.infra.UserDetailsServiceHelper;
import com.chutneytesting.security.infra.memory.InMemoryUsersProperties;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class OAuth2UserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2UserDetailsService.class);
    private final AuthenticationService authenticationService;

    public OAuth2UserDetailsService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDto user = new UserDto();
        user.setId(username);
        user.setName(username);
        user.setRoles(Collections.emptySet());
        UserDetailsServiceHelper.grantAuthoritiesFromUserRole(user, authenticationService);
        return null;
    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.infra.sso;


import com.chutneytesting.security.AbstractChutneyWebSecurityConfig;
import com.chutneytesting.security.domain.AuthenticationService;
import com.chutneytesting.security.domain.Authorizations;
import java.util.Collections;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@Profile("sso-auth")
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties({OAuth2AuthorizationServerProperties.class, SsoOpenIdConnectConfigProperties.class})
public class OAuth2SsoSecurityConfiguration extends AbstractChutneyWebSecurityConfig {

    @Bean
    public AuthenticationService authenticationService(Authorizations authorizations) {
        return new AuthenticationService(authorizations);
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> customOAuth2UserService(AuthenticationService authenticationService) {
        return new OAuth2SsoUserService(authenticationService);
    }

    @Bean
    public OAuth2TokenAuthenticationProvider tokenAuthenticationProvider(AuthenticationService authenticationService, ClientRegistrationRepository clientRegistrationRepository) {
        return new OAuth2TokenAuthenticationProvider(customOAuth2UserService(authenticationService), clientRegistrationRepository.findByRegistrationId("my-provider"));
    }

    @Bean
    public AuthenticationManager authenticationManager(OAuth2TokenAuthenticationProvider OAuth2TokenAuthenticationProvider) {
        return new ProviderManager(Collections.singletonList(OAuth2TokenAuthenticationProvider));
    }

    @Bean
    public SecurityFilterChain securityFilterChainOAuth2Sso(final HttpSecurity http, OAuth2TokenAuthenticationProvider OAuth2TokenAuthenticationProvider, AuthenticationManager authenticationManager) throws Exception {
        OAuth2TokenAuthenticationFilter tokenFilter = new OAuth2TokenAuthenticationFilter(authenticationManager);
        http.authenticationProvider(OAuth2TokenAuthenticationProvider)
            .addFilterBefore(tokenFilter, BasicAuthenticationFilter.class)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
        configureHttp(http);
        return http.build();
    }
}

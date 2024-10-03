/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.infra.sso;

import com.chutneytesting.security.domain.AuthenticationService;
import com.chutneytesting.security.infra.memory.InMemoryUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("sso-auth")
public class OAuth2SsoSecurityConfiguration {

    @Bean
    @Primary
    SsoOpenIdConnectConfig ssoOpenIdConnectConfig(
        @Value("${auth.sso.issuer}") String issuer,
        @Value("${auth.sso.clientId}") String clientId,
        @Value("${auth.sso.clientSecret}") String clientSecret,
        @Value("${auth.sso.responseType}") String responseType,
        @Value("${auth.sso.scope}") String scope,
        @Value("${auth.sso.redirectBaseUrl}") String redirectUri,
        @Value("${auth.sso.oidc}") Boolean oidc,
        @Value("${auth.sso.ssoProviderName}") String ssoProviderName
    ) {
        return new SsoOpenIdConnectConfig(issuer,
            clientId,
            clientSecret,
            responseType,
            scope,
            redirectUri,
            ssoProviderName,
            oidc);
    }

    @Bean
    public OAuth2UserDetailsService oAuth2UserDetailsService(AuthenticationService authenticationService) {
        return new OAuth2UserDetailsService(authenticationService);
    }

    @Configuration
    @Profile("sso-auth")
    public static class OAuth2SsoConfiguration {

        @Autowired
        protected void configure(
            final AuthenticationManagerBuilder auth,
            final InMemoryUserDetailsService authService
        ) throws Exception {
            auth.userDetailsService(authService);
        }

        @Bean
        public SecurityFilterChain securityFilterChainOAuth2Sso(final HttpSecurity http, OAuth2UserDetailsService oAuth2UserDetailsService) throws Exception {
            return http.oauth2ResourceServer(oauth2ResourceServerCustomizer -> oauth2ResourceServerCustomizer.jwt(Customizer.withDefaults()))
                .userDetailsService(oAuth2UserDetailsService)
                .build();
        }
    }
}

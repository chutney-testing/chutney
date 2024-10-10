/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security.infra.sso;

import static com.chutneytesting.security.ChutneyWebSecurityConfig.API_BASE_URL_PATTERN;
import static com.chutneytesting.security.ChutneyWebSecurityConfig.LOGIN_URL;
import static com.chutneytesting.security.ChutneyWebSecurityConfig.LOGOUT_URL;

import com.chutneytesting.admin.api.InfoController;
import com.chutneytesting.security.ChutneyWebSecurityConfig;
import com.chutneytesting.security.api.SsoOpenIdConnectController;
import com.chutneytesting.security.api.UserDto;
import com.chutneytesting.security.domain.AuthenticationService;
import com.chutneytesting.security.domain.Authorizations;
import com.chutneytesting.server.core.domain.security.Authorization;
import java.util.ArrayList;
import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.security.oauth2.server.servlet.OAuth2AuthorizationServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.Customizer;
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
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@Profile("sso-auth")
@EnableWebSecurity
@EnableMethodSecurity
@EnableConfigurationProperties(OAuth2AuthorizationServerProperties.class)
public class OAuth2SsoSecurityConfiguration {

    @Value("${management.endpoints.web.base-path:/actuator}")
    public static String ACTUATOR_BASE_URL;

    @Value("${server.ssl.enabled:true}")
    Boolean sslEnabled;

    @Bean
    public AuthenticationService authenticationService(Authorizations authorizations) {
        return new AuthenticationService(authorizations);
    }

    @Bean
    @ConditionalOnMissingBean
    public SsoOpenIdConnectConfig emptySsoOpenIdConnectConfig() {
        return new SsoOpenIdConnectConfig();
    }

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
    @Order(1)
    public SecurityFilterChain securityFilterChainOAuth2Sso(final HttpSecurity http, OAuth2TokenAuthenticationProvider OAuth2TokenAuthenticationProvider, AuthenticationManager authenticationManager) throws Exception {
        ChutneyWebSecurityConfig chutneyWebSecurityConfig = new ChutneyWebSecurityConfig();
        OAuth2TokenAuthenticationFilter tokenFilter = new OAuth2TokenAuthenticationFilter(authenticationManager);
        chutneyWebSecurityConfig.configureBaseHttpSecurity(http, sslEnabled);
        UserDto anonymous = chutneyWebSecurityConfig.anonymous();
        http
            .authenticationProvider(OAuth2TokenAuthenticationProvider)
            .addFilterBefore(tokenFilter, BasicAuthenticationFilter.class)
            .anonymous(anonymousConfigurer -> anonymousConfigurer
                .principal(anonymous)
                .authorities(new ArrayList<>(anonymous.getAuthorities())))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(httpRequest -> {
                HandlerMappingIntrospector introspector = new HandlerMappingIntrospector();
                httpRequest
                    .requestMatchers(new MvcRequestMatcher(introspector, LOGIN_URL)).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, LOGOUT_URL)).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, InfoController.BASE_URL + "/**")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, SsoOpenIdConnectController.BASE_URL)).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, SsoOpenIdConnectController.BASE_URL + "/**")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, API_BASE_URL_PATTERN)).authenticated()
                    .requestMatchers(new MvcRequestMatcher(introspector, ACTUATOR_BASE_URL + "/**")).hasAuthority(Authorization.ADMIN_ACCESS.name())
                    .anyRequest().permitAll();
            })
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.security;

import com.chutneytesting.admin.api.InfoController;
import com.chutneytesting.security.api.SsoOpenIdConnectController;
import com.chutneytesting.security.api.UserController;
import com.chutneytesting.security.api.UserDto;
import com.chutneytesting.security.domain.SsoOpenIdConnectConfigService;
import com.chutneytesting.security.infra.handlers.Http401FailureHandler;
import com.chutneytesting.security.infra.handlers.HttpEmptyLogoutSuccessHandler;
import com.chutneytesting.security.infra.handlers.HttpLoginSuccessHandler;
import com.chutneytesting.security.infra.sso.SsoOpenIdConnectConfigProperties;
import com.chutneytesting.server.core.domain.security.Authorization;
import com.chutneytesting.server.core.domain.security.User;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.ChannelSecurityConfigurer;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

public abstract class AbstractChutneyWebSecurityConfig {

    protected static final String LOGIN_URL = UserController.BASE_URL + "/login";
    protected static final String LOGOUT_URL = UserController.BASE_URL + "/logout";
    protected static final String API_BASE_URL_PATTERN = "/api/**";

    @Bean
    public SsoOpenIdConnectConfigService ssoOpenIdConnectConfigService(@Nullable SsoOpenIdConnectConfigProperties ssoOpenIdConnectConfigProperties) {
        return new SsoOpenIdConnectConfigService(ssoOpenIdConnectConfigProperties);
    }

    @Value("${management.endpoints.web.base-path:/actuator}")
    protected String actuatorBaseUrl;

    @Value("${server.ssl.enabled:true}")
    private Boolean sslEnabled;

    protected HttpSecurity configureHttp(final HttpSecurity http) throws Exception {
        configureBaseHttpSecurity(http);
        UserDto anonymous = anonymous();
        http
            .anonymous(anonymousConfigurer -> anonymousConfigurer
                .principal(anonymous)
                .authorities(new ArrayList<>(anonymous.getAuthorities())))
            .authorizeHttpRequests(httpRequest -> {
                HandlerMappingIntrospector introspector = new HandlerMappingIntrospector();
                httpRequest
                    .requestMatchers(new MvcRequestMatcher(introspector, LOGIN_URL)).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, LOGOUT_URL)).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, InfoController.BASE_URL + "/**")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, SsoOpenIdConnectController.BASE_URL + "/**")).permitAll()
                    .requestMatchers(new MvcRequestMatcher(introspector, API_BASE_URL_PATTERN)).authenticated()
                    .requestMatchers(new MvcRequestMatcher(introspector, actuatorBaseUrl + "/**")).hasAuthority(Authorization.ADMIN_ACCESS.name())
                    .anyRequest().permitAll();
            })
            .httpBasic(Customizer.withDefaults());
        return http;
    }

    protected void configureBaseHttpSecurity(final HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .requiresChannel(this.requireChannel(sslEnabled))
            .formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
                .loginProcessingUrl(LOGIN_URL)
                .successHandler(new HttpLoginSuccessHandler())
                .failureHandler(new Http401FailureHandler()))
            .logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                .logoutUrl(LOGOUT_URL)
                .logoutSuccessHandler(new HttpEmptyLogoutSuccessHandler()));
    }

    protected UserDto anonymous() {
        UserDto anonymous = new UserDto();
        anonymous.setId(User.ANONYMOUS.id);
        anonymous.setName(User.ANONYMOUS.id);
        anonymous.grantAuthority("ANONYMOUS");
        return anonymous;
    }

    private Customizer<ChannelSecurityConfigurer<HttpSecurity>.ChannelRequestMatcherRegistry> requireChannel(Boolean sslEnabled) {
        if (sslEnabled) {
            return channelRequestMatcherRegistry -> channelRequestMatcherRegistry.anyRequest().requiresSecure();
        } else {
            return channelRequestMatcherRegistry -> channelRequestMatcherRegistry.anyRequest().requiresInsecure();
        }
    }
}

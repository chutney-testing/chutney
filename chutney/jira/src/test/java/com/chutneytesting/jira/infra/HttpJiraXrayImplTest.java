/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */
package com.chutneytesting.jira.infra;

import static com.github.tomakehurst.wiremock.client.WireMock.anyRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.anyUrl;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.chutneytesting.jira.domain.JiraServerConfiguration;
import com.chutneytesting.jira.xrayapi.Xray;
import com.chutneytesting.jira.xrayapi.XrayInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import java.net.UnknownHostException;
import java.util.Base64;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class HttpJiraXrayImplTest {

    @RegisterExtension
    static WireMockExtension proxyMock = WireMockExtension.newInstance()
        .options(
            wireMockConfig()
                .httpDisabled(true)
                .dynamicHttpsPort()
        )
        .failOnUnmatchedRequests(true)
        .build();

    @Nested
    @DisplayName("No proxy")
    class NoProxy {
        @Test
        void update_xray_execution() {
            // Given
            JiraServerConfiguration config = new JiraServerConfiguration(
                "http://fake-server-jira",
                "user",
                "password",
                "",
                "",
                ""
            );

            // When/Then
            HttpJiraXrayImpl httpJiraXray = new HttpJiraXrayImpl(config);
            assertThatThrownBy(() ->
                httpJiraXray.updateRequest(new Xray("test", List.of(), new XrayInfo(List.of())))
            )
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("Unable to update test execution [test] : ")
                .hasRootCauseExactlyInstanceOf(UnknownHostException.class);
        }

        @Test
        void test_issue_as_test_plan() {
            // Given
            String issueId = "PRJ-666";

            var config = new JiraServerConfiguration(
                "http://fake-server-jira",
                "user",
                "password",
                null,
                "",
                ""
            );

            // When/Then
            var sut = new HttpJiraXrayImpl(config);
            assertThatThrownBy(() ->
                sut.isTestPlan(issueId)
            )
                .isExactlyInstanceOf(RuntimeException.class)
                .hasMessage("Unable to get issue [PRJ-666] : ")
                .hasRootCauseExactlyInstanceOf(UnknownHostException.class);
        }

        @Test
        void test_empty_issue_as_test_plan() {
            // Given
            String issueId = "";

            var config = new JiraServerConfiguration(
                "http://fake-server-jira",
                "user",
                "password",
                null,
                "",
                ""
            );
            var sut = new HttpJiraXrayImpl(config);

            // When
            boolean result = sut.isTestPlan(issueId);

            // Then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("Proxy without authentication")
    class ProxyWithoutAuth {
        @Test
        void update_xray_execution() {
            // Given
            JiraServerConfiguration config = new JiraServerConfiguration(
                "http://fake-server-jira",
                "user",
                "password",
                proxyMock.baseUrl(),
                "",
                ""
            );

            proxyMock.stubFor(
                post(urlPathMatching("/rest/raven/1.0/import/execution"))
                    .willReturn(okJson("1234"))
            );

            // When
            HttpJiraXrayImpl httpJiraXray = new HttpJiraXrayImpl(config);
            httpJiraXray.updateRequest(new Xray("test", List.of(), new XrayInfo(List.of())));

            // Then
            proxyMock.verify(
                1, postRequestedFor(anyUrl())
                    .withHeader("Authorization", equalTo(expectedAuthorization(config)))
                    .withoutHeader("Proxy-Authorization")
            );
        }

        @Test
        void test_issue_as_test_plan() {
            // Given
            String issueId = "PRJ-666";

            var config = new JiraServerConfiguration(
                "http://fake-server-jira",
                "user",
                "password",
                proxyMock.baseUrl(),
                "",
                ""
            );

            proxyMock.stubFor(
                get(urlPathMatching("/rest/api/latest/issue/" + issueId + ".*"))
                    .willReturn(okJson("""
                        {
                            "self": "...",
                            "key": "1234",
                            "id": 1234,
                            "expand": "one,two",
                            "fields": {
                                "summary": "",
                                "issuetype": {
                                    "self": "...",
                                    "id": 123,
                                    "name": "Test Plan",
                                    "subtask": false
                                },
                                "created": "2024-01-01T00:00:00.000Z",
                                "updated": "2024-01-01T00:00:00.000Z",
                                "project": {
                                    "self": "...",
                                    "key": ""
                                },
                                "status": {
                                    "self": "...",
                                    "name": "",
                                    "description": "",
                                    "iconUrl": "http://host/icon"
                                }
                            },
                            "names": {
                            },
                            "schema": {
                            }
                        }
                        """.stripIndent()
                    ))
            );

            proxyMock.stubFor(
                get(urlPathMatching("/rest/api/latest/issuetype"))
                    .willReturn(okJson("""
                        [
                            {
                                "self": "...",
                                "id": 321,
                                "name": "fakeType",
                                "subtask": false
                            },
                            {
                                "self": "...",
                                "id": 123,
                                "name": "Test Plan",
                                "subtask": false
                            }
                        ]
                        """.stripIndent()
                    ))
            );

            // When
            var sut = new HttpJiraXrayImpl(config);
            boolean isTestPlan = sut.isTestPlan(issueId);

            // Then
            proxyMock.verify(
                2, anyRequestedFor(anyUrl())
                    .withHeader("Authorization", equalTo(expectedAuthorization(config)))
                    .withoutHeader("Proxy-Authorization")
            );
            assertThat(isTestPlan).isTrue();
        }
    }

    @Nested
    @DisplayName("Proxy with authentication")
    class ProxyWithAuth {
        @Test
        void update_xray_execution() {
            // Given
            JiraServerConfiguration config = new JiraServerConfiguration(
                "http://fake-server-jira",
                "user",
                "password",
                proxyMock.baseUrl(),
                "userProxy",
                "passwordProxy"
            );

            proxyMock.stubFor(
                post(urlPathMatching("/rest/raven/1.0/import/execution"))
                    .willReturn(okJson("1234"))
            );

            // When
            HttpJiraXrayImpl httpJiraXray = new HttpJiraXrayImpl(config);
            httpJiraXray.updateRequest(new Xray("test", List.of(), new XrayInfo(List.of())));

            // Then
            proxyMock.verify(
                1, postRequestedFor(anyUrl())
                    .withHeader("Authorization", equalTo(expectedAuthorization(config)))
                    .withHeader("Proxy-Authorization", equalTo(expectedProxyAuthorization(config)))
            );
        }

        @Test
        void test_issue_as_test_plan() {
            // Given
            String issueId = "PRJ-666";

            var config = new JiraServerConfiguration(
                "http://fake-server-jira",
                "user",
                "password",
                proxyMock.baseUrl(),
                "userProxy",
                "passwordProxy"
            );

            proxyMock.stubFor(
                get(urlPathMatching("/rest/api/latest/issue/" + issueId + ".*"))
                    .willReturn(okJson("""
                        {
                            "self": "...",
                            "key": "1234",
                            "id": 1234,
                            "expand": "one,two",
                            "fields": {
                                "summary": "",
                                "issuetype": {
                                    "self": "...",
                                    "id": 123,
                                    "name": "Test Plan",
                                    "subtask": false
                                },
                                "created": "2024-01-01T00:00:00.000Z",
                                "updated": "2024-01-01T00:00:00.000Z",
                                "project": {
                                    "self": "...",
                                    "key": ""
                                },
                                "status": {
                                    "self": "...",
                                    "name": "",
                                    "description": "",
                                    "iconUrl": "http://host/icon"
                                }
                            },
                            "names": {
                            },
                            "schema": {
                            }
                        }
                        """.stripIndent()
                    ))
            );

            proxyMock.stubFor(
                get(urlPathMatching("/rest/api/latest/issuetype"))
                    .willReturn(okJson("""
                        [
                            {
                                "self": "...",
                                "id": 321,
                                "name": "fakeType",
                                "subtask": false
                            },
                            {
                                "self": "...",
                                "id": 123,
                                "name": "Test Plan",
                                "subtask": false
                            }
                        ]
                        """.stripIndent()
                    ))
            );

            // When
            var sut = new HttpJiraXrayImpl(config);
            boolean isTestPlan = sut.isTestPlan(issueId);

            // Then
            proxyMock.verify(
                2, anyRequestedFor(anyUrl())
                    .withHeader("Authorization", equalTo(expectedAuthorization(config)))
                    .withHeader("Proxy-Authorization", equalTo(expectedProxyAuthorization(config)))
            );
            assertThat(isTestPlan).isTrue();
        }
    }

    private static String expectedProxyAuthorization(JiraServerConfiguration config) {
        return "Basic " + Base64.getEncoder()
            .encodeToString((config.userProxy() + ":" + config.passwordProxy()).getBytes());
    }

    private static String expectedAuthorization(JiraServerConfiguration config) {
        return "Basic " + Base64.getEncoder()
            .encodeToString((config.username() + ":" + config.password()).getBytes());
    }
}

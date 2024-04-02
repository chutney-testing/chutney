/*
 *  Copyright 2017-2023 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.chutneytesting.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(OutputCaptureExtension.class)
public class AuditHandlerTest {

    Authentication mockAuth = mock(Authentication.class);
    static Logger auditHandlerLogger = ((LoggerContext) LoggerFactory.getILoggerFactory()).getLogger(AuditHandler.class);

    @BeforeAll
    public static void logLevelToInfo() {
        auditHandlerLogger.setLevel(Level.INFO);
    }

    @AfterAll
    public static void resetLogLevel() {
        auditHandlerLogger.setLevel(Level.WARN);
    }

    @BeforeEach
    public void setUp() {
        Mockito.reset(mockAuth);
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    public void should_log_post(CapturedOutput output) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/test/endpoint/42/entity");

        MockHttpServletResponse response = new MockHttpServletResponse();
        AuditHandler handler = new AuditHandler();

        userAuthentified("user authentified");

        boolean preHandled = handler.preHandle(request, response, null);

        assertTrue(preHandled);
        assertThat(output.getAll()).contains("[user authentified] [POST] [/test/endpoint/42/entity]");
    }

    @Test
    public void should_not_log_if_no_auth(CapturedOutput output) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuditHandler handler = new AuditHandler();

        boolean preHandled = handler.preHandle(request, response, null);

        assertTrue(preHandled);
        assertThat(output.getAll()).isEmpty();
    }

    @Test
    public void should_not_log_get_request(CapturedOutput output) throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/test");
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuditHandler handler = new AuditHandler();

        userAuthentified("user");

        boolean preHandled = handler.preHandle(request, response, null);

        assertTrue(preHandled);
        assertThat(output.getAll()).isEmpty();
    }

    private void userAuthentified(String username) {
        when(mockAuth.getName()).thenReturn(username);
        SecurityContextHolder.getContext().setAuthentication(mockAuth);
    }

}

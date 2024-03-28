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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

@Component
public class AuditHandler implements HandlerInterceptor {
    Logger logger = LoggerFactory.getLogger(AuditHandler.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!"GET".equals(request.getMethod())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                logger.info("[" + authentication.getName() + "] [" + request.getMethod() + "] [" + request.getRequestURI() + "] " + listParameters(request) + listPathVariable(request));
            }
        }

        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    private String listParameters(HttpServletRequest request) {
        Enumeration<String> parameterNames = request.getParameterNames();
        Map<String, String> parameterMap = new HashMap<>();
        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            parameterMap.put(parameterName, request.getParameter(parameterName));
        }
        return joinEntries(parameterMap);
    }


    private String listPathVariable(HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        return joinEntries(pathVariables);
    }


    private String joinEntries(Map<String, String> entries) {
        StringBuilder sb = new StringBuilder();
        if (entries != null) {
            for (Map.Entry<String, String> entry : entries.entrySet()) {
                String value = cleanPassword(entry);
                sb.append("[").append(entry.getKey()).append(" = ").append(value).append("] ");
            }
        }
        return sb.toString();
    }

    private String cleanPassword(Map.Entry<String, String> entry) {
        return entry.getKey().toLowerCase()
            .matches("(.*(password|pass|pwd).*)") ? "***" : entry.getValue();
    }

}

/*
 * Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chutneytesting.action.http;

import static com.chutneytesting.action.function.SoapFunction.soapInsertWSUsernameToken;
import static com.chutneytesting.action.http.HttpActionHelper.httpCommonValidation;
import static com.chutneytesting.action.spi.time.Duration.parseToMs;
import static com.chutneytesting.action.spi.validation.Validator.getErrorsFrom;
import static java.util.Optional.ofNullable;

import com.chutneytesting.action.http.domain.HttpAction;
import com.chutneytesting.action.http.domain.HttpClient;
import com.chutneytesting.action.http.domain.HttpClientFactory;
import com.chutneytesting.action.spi.Action;
import com.chutneytesting.action.spi.ActionExecutionResult;
import com.chutneytesting.action.spi.injectable.Input;
import com.chutneytesting.action.spi.injectable.Logger;
import com.chutneytesting.action.spi.injectable.Target;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

public class HttpSoapAction implements Action {

    private static final String DEFAULT_TIMEOUT = "2000 ms";

    private final Target target;
    private final Logger logger;
    private final String uri;
    private final String username;
    private final String password;
    private final Object body;
    private final String timeout;
    private final Map<String, String> headers;

    public HttpSoapAction(Target target, Logger logger,
                        @Input("uri") String uri,
                        @Input("body") String body,
                        @Input("username") String username,
                        @Input("password") String password,
                        @Input("timeout") String timeout,
                        @Input("headers") Map<String, String> headers) {
        this.target = target;
        this.logger = logger;
        this.uri = uri;
        this.body = body;
        this.username = username;
        this.password = password;
        this.timeout = ofNullable(timeout).orElse(DEFAULT_TIMEOUT);
        this.headers = headers != null ? headers : new HashMap<>();
    }

    @Override
    public List<String> validateInputs() {
        return getErrorsFrom(
            httpCommonValidation(target, timeout)
        );
    }

    @Override
    public ActionExecutionResult execute() {
        HttpClient httpClient = new HttpClientFactory().create(logger, target, String.class, (int) parseToMs(timeout));
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(httpHeaders::add);
        Object envelope = soapInsertWSUsernameToken(this.username, this.password, body.toString());
        Supplier<ResponseEntity<String>> caller = () -> httpClient.post(this.uri, ofNullable(envelope).orElse("{}"), httpHeaders);
        return HttpAction.httpCall(logger, caller);
    }
}

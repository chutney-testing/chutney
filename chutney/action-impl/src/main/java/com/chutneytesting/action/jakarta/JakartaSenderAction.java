/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.jakarta;

import static com.chutneytesting.action.spi.validation.ActionValidatorsUtils.notBlankStringValidation;
import static com.chutneytesting.action.spi.validation.ActionValidatorsUtils.targetValidation;
import static com.chutneytesting.action.spi.validation.Validator.getErrorsFrom;

import com.chutneytesting.action.spi.Action;
import com.chutneytesting.action.spi.ActionExecutionResult;
import com.chutneytesting.action.spi.injectable.Input;
import com.chutneytesting.action.spi.injectable.Logger;
import com.chutneytesting.action.spi.injectable.Target;
import com.chutneytesting.tools.CloseableResource;
import jakarta.jms.JMSException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JakartaSenderAction implements Action {

    private final Target target;
    private final Logger logger;

    private final String destination;
    private final String body;
    private final Map<String, String> headers;

    // TODO create injectable service
    private final JakartaConnectionFactory jmsConnectionFactory = new JakartaConnectionFactory();

    public JakartaSenderAction(Target target, Logger logger, @Input("destination") String destination, @Input("body") String body, @Input("headers") Map<String, String> headers) {
        this.target = target;
        this.logger = logger;
        this.destination = destination;
        this.body = body;
        this.headers = headers != null ? headers : Collections.emptyMap();
    }

    @Override
    public List<String> validateInputs() {
        return getErrorsFrom(
            targetValidation(target),
            notBlankStringValidation(destination, "destination"),
            notBlankStringValidation(body, "body")
        );
    }

    @Override
    public ActionExecutionResult execute() {
        try (CloseableResource<JakartaConnectionFactory.MessageSender> producer = jmsConnectionFactory.getMessageProducer(target, destination)) {
            producer.getResource().send(body, headers);
            logger.info("Successfully sent message on " + destination + " to " + target.name() + " (" + target.uri().toString() + ")");
            return ActionExecutionResult.ok();
        } catch (JMSException | UncheckedJakartaException e) {
            logger.error(e);
            return ActionExecutionResult.ko();
        }
    }

}

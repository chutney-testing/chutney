/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.jakarta.consumer;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import java.util.Optional;

public interface Consumer {

    Optional<Message> getMessage() throws JMSException;
}

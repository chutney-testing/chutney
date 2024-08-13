/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.jakarta.consumer.bodySelector;

import jakarta.jms.Message;

public interface BodySelector {

    boolean match(Message message);
}

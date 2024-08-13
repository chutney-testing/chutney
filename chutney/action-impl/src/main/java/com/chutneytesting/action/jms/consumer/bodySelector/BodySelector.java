/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.jms.consumer.bodySelector;

import javax.jms.Message;

public interface BodySelector {

    boolean match(Message message);
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.domain;

import java.util.List;

/**
 * Thrown when a action fails to be instantiated.
 *
 * @see ActionTemplate#create(List)
 */
public class ActionInstantiationFailureException extends RuntimeException {

    public ActionInstantiationFailureException(String actionIdentifier, ReflectiveOperationException cause) {
        super("Unable to instantiate Action[" + actionIdentifier + "]: " + cause.getMessage(), cause);
    }

}

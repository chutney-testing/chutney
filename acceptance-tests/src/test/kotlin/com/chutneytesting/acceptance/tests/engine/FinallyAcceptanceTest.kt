/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.engine

import com.chutneytesting.kotlin.dsl.FinalAction
import com.chutneytesting.kotlin.dsl.Scenario

/**
 * this scenario proves that there is no infinite-loop when a {@link FinallyAction} registers another {@link FinallyAction} with the same identifier
 */
val `Step of a type self registering as Finally Action does not create an infinite loop` = Scenario(title = "Step of a type self registering as Finally Action does not create an infinite loop") {
  When("Register finally action") {
    FinalAction(
      name = "Finally action",
      type =  "self-registering-finally"
    )
  }
}


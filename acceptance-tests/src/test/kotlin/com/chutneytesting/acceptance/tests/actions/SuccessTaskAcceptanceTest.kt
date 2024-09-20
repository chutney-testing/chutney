/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.actions

import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.SuccessAction

val `Direct Success` = Scenario(title = "Direct Success") {

  When("Direct success") {
    SuccessAction()
  }
}

val `Substeps Success` = Scenario(title = "Substeps Success") {
  Given("Direct success") {
    SuccessAction()
  }
  When(" I want to have one substep") {
    SuccessAction()
  }
  Then("I want to have more multiple substeps") {
    Step("first substep") {
      SuccessAction()
    }
    Step("second substep") {
      SuccessAction()
    }
  }
}
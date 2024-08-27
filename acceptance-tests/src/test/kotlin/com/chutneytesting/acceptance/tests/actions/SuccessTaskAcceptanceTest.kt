/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.acceptance.tests.actions

import com.chutneytesting.acceptance.common.checkScenarioSuccess
import com.chutneytesting.acceptance.common.executeScenario
import com.chutneytesting.kotlin.dsl.DebugAction
import com.chutneytesting.kotlin.dsl.Scenario
import com.chutneytesting.kotlin.dsl.SuccessAction

val `Direct Success` = Scenario(title = "Direct Success") {

  When("Direct success") {
    DebugAction()
  }
}

val `Substeps Success` = Scenario(title = "Substeps Success") {
  Given("Direct success") {
    DebugAction()
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
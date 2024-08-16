/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.delegation;


import com.chutneytesting.engine.domain.execution.engine.step.Step;
import com.chutneytesting.engine.domain.execution.report.StepExecutionReport;

public interface DelegationClient {

    StepExecutionReport handDown(Step stepDefinition, NamedHostAndPort delegate) throws CannotDelegateException;

}

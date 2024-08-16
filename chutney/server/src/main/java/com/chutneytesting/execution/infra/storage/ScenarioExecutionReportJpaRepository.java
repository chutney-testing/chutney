/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.infra.storage;

import com.chutneytesting.execution.infra.storage.jpa.ScenarioExecutionReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ScenarioExecutionReportJpaRepository extends JpaRepository<ScenarioExecutionReportEntity, Long>, JpaSpecificationExecutor<ScenarioExecutionReportEntity> {
}

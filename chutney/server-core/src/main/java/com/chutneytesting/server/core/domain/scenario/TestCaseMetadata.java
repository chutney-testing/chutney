/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.server.core.domain.scenario;

import java.time.Instant;
import java.util.List;

public interface TestCaseMetadata {

    String id(); // TODO - to extract

    String defaultDataset();

    String title();

    String description();

    Instant creationDate();

    List<String> tags();

    String author();

    Instant updateDate();

    Integer version();

}

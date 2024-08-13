/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.domain;

import com.chutneytesting.scenario.domain.raw.RawTestCase;
import com.chutneytesting.server.core.domain.execution.ExecutionRequest;
import com.chutneytesting.server.core.domain.execution.processor.TestCasePreProcessor;
import com.chutneytesting.server.core.domain.globalvar.GlobalvarRepository;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

@Component
public class RawDataSetPreProcessor implements TestCasePreProcessor<RawTestCase> {

    private final GlobalvarRepository globalvarRepository;

    public RawDataSetPreProcessor(GlobalvarRepository globalvarRepository) {
        this.globalvarRepository = globalvarRepository;
    }

    @Override
    public RawTestCase apply(ExecutionRequest executionRequest) {
        RawTestCase testCase = (RawTestCase) executionRequest.testCase;
        return RawTestCase.builder()
            .withMetadata(testCase.metadata)
            .withScenario(replaceParams(globalvarRepository.getFlatMap(), testCase.scenario, StringEscapeUtils::escapeJson))
            .build();
    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.glacio.domain.parser.executable.common;

import static com.chutneytesting.glacio.domain.parser.ParsingContext.PARSING_CONTEXT_KEYS.ENVIRONMENT;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.chutneytesting.engine.api.execution.TargetExecutionDto;
import com.chutneytesting.engine.domain.environment.TargetImpl;
import com.chutneytesting.environment.api.target.TargetApi;
import com.chutneytesting.environment.api.target.dto.TargetDto;
import com.chutneytesting.glacio.domain.parser.ParsingContext;
import com.github.fridujo.glacio.model.Step;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TargetStepParserTest {

    private static final ParsingContext CONTEXT = new ParsingContext();
    public static final String ENV = "ENV";
    private TargetApi targetApi;

    private TargetStepParser sut;

    @BeforeEach
    public void setUp() {
        CONTEXT.values.put(ENVIRONMENT, "ENV");
        targetApi = mock(TargetApi.class);
        sut = new TargetStepParser(targetApi, "On");
    }

    @Test
    public void should_build_target_from_step_by_name() {
        TargetDto expectedTarget = new TargetDto("My target name", "http://url:8080", null);

        Step stepParent = mock(Step.class);
        Step step = mock(Step.class);
        when(stepParent.getSubsteps()).thenReturn(singletonList(step));
        when(step.getText()).thenReturn("On " + expectedTarget.name);
        when(targetApi.getTarget(ENV, expectedTarget.name))
            .thenReturn(expectedTarget);

        TargetExecutionDto targetFound = sut.parseGlacioStep(CONTEXT, stepParent);

        assertThat(targetFound).isNotEqualTo(TargetImpl.NONE);
        assertThat(targetFound.name).isEqualTo(expectedTarget.name);
        assertThat(targetFound.url).isEqualTo(expectedTarget.url);
    }

}

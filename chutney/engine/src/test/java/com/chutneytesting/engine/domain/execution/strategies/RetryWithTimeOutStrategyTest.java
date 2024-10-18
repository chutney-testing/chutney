/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.domain.execution.strategies;

import static com.chutneytesting.engine.domain.execution.ScenarioExecution.createScenarioExecution;
import static com.chutneytesting.engine.domain.execution.report.Status.FAILURE;
import static com.chutneytesting.engine.domain.execution.report.Status.SUCCESS;
import static java.util.Collections.emptyMap;
import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalMatchers.and;
import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chutneytesting.ExecutionConfiguration;
import com.chutneytesting.engine.api.execution.ExecutionRequestDto;
import com.chutneytesting.engine.api.execution.StatusDto;
import com.chutneytesting.engine.api.execution.StepExecutionReportDto;
import com.chutneytesting.engine.api.execution.TestEngine;
import com.chutneytesting.engine.domain.execution.ScenarioExecution;
import com.chutneytesting.engine.domain.execution.engine.evaluation.EvaluationException;
import com.chutneytesting.engine.domain.execution.engine.evaluation.StepDataEvaluator;
import com.chutneytesting.engine.domain.execution.engine.scenario.ScenarioContextImpl;
import com.chutneytesting.engine.domain.execution.engine.step.Step;
import com.chutneytesting.engine.domain.execution.report.Status;
import com.chutneytesting.tools.Jsons;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.util.ReflectionUtils;

public class RetryWithTimeOutStrategyTest {

    private final RetryWithTimeOutStrategy strategyUnderTest = new RetryWithTimeOutStrategy();

    private static StrategyProperties properties(String timeOut, String retryDelay) {
        StrategyProperties strategyProperties = new StrategyProperties();
        strategyProperties.setProperty("timeOut", timeOut);
        strategyProperties.setProperty("retryDelay", retryDelay);

        return strategyProperties;
    }

    // TODO remove this test as dup of DurationTest ?
    @Test
    public void fails_because_of_negative_parameters_durations() {
        StrategyProperties strategyProperties = properties("-1", "50 ms");
        StepStrategyDefinition strategyDefinition = new StepStrategyDefinition("", strategyProperties);
        Step step = mock(Step.class);
        when(step.strategy()).thenReturn(Optional.of(strategyDefinition));
        when(step.dataEvaluator()).thenReturn(new StepDataEvaluator(null));
        assertThatThrownBy(() -> strategyUnderTest.execute(createScenarioExecution(null), step, new ScenarioContextImpl(), null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void fails_because_missing_parameters_durations() {
        StrategyProperties strategyProperties = properties(null, "50 ms");
        StepStrategyDefinition strategyDefinition = new StepStrategyDefinition("", strategyProperties);

        Step step = mock(Step.class);
        when(step.strategy()).thenReturn(Optional.of(strategyDefinition));
        assertThatThrownBy(() -> strategyUnderTest.execute(createScenarioExecution(null), step, new ScenarioContextImpl(), null))
            .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void step_succeeds_execute_only_once() {
        StrategyProperties strategyProperties = properties("100 sec", "50 ms");
        StepStrategyDefinition strategyDefinition = new StepStrategyDefinition("", strategyProperties);

        Step step = mock(Step.class);
        when(step.strategy()).thenReturn(Optional.of(strategyDefinition));
        when(step.dataEvaluator()).thenReturn(new StepDataEvaluator(null));
        strategyUnderTest.execute(createScenarioExecution(null), step, new ScenarioContextImpl(), null);

        verify(step, times(1)).execute(any(), any(), any());
    }

    @Test
    public void step_fails_retry_until_timeout_exceed() {
        StrategyProperties strategyProperties = properties("1 sec", "50 ms");
        StepStrategyDefinition strategyDefinition = new StepStrategyDefinition("", strategyProperties);

        Step step = mockStep(FAILURE);
        when(step.strategy()).thenReturn(Optional.of(strategyDefinition));
        long start = System.currentTimeMillis();
        ScenarioExecution scenarioExecution = createScenarioExecution(null);
        strategyUnderTest.execute(scenarioExecution, step, new ScenarioContextImpl(), null);

        long executionDuration = System.currentTimeMillis() - start;
        assertThat(executionDuration).isBetween(1000L, 2000L);
        verify(step, atMost(20)).execute(any(), any());
        verify(step, atMost(19)).resetExecution();
    }

    @Test
    public void step_fails_do_not_retry_if_stop_requested() {
        StrategyProperties strategyProperties = properties("0.1 sec", "5 ms");
        StepStrategyDefinition strategyDefinition = new StepStrategyDefinition("", strategyProperties);

        Step step = mockStep(FAILURE);
        when(step.strategy()).thenReturn(Optional.of(strategyDefinition));
        ScenarioExecution scenarioExecution = createScenarioExecution(null);
        stopExecution(scenarioExecution);
        Status stepExecutedStatus = strategyUnderTest.execute(scenarioExecution, step, new ScenarioContextImpl(), null);

        verify(step, times(1)).execute(any(), any(), any());
        assertThat(stepExecutedStatus).isEqualTo(Status.STOPPED);
    }

    @Test
    public void step_fails_retry_until_success_execute_4_times() {
        StrategyProperties strategyProperties = properties("1 sec", "5 ms");
        StepStrategyDefinition strategyDefinition = new StepStrategyDefinition("", strategyProperties);

        Step step = mockStep(FAILURE, FAILURE, FAILURE, SUCCESS);
        when(step.strategy()).thenReturn(Optional.of(strategyDefinition));
        strategyUnderTest.execute(createScenarioExecution(null), step, new ScenarioContextImpl(), null);

        verify(step, times(4)).execute(any(), any(), any());
    }

    @Test
    public void step_should_keep_last_error_message() {
        StrategyProperties strategyProperties = properties("1 sec", "5 ms");
        StepStrategyDefinition strategyDefinition = new StepStrategyDefinition("", strategyProperties);

        Step step = mockStep(FAILURE, FAILURE, SUCCESS);
        when(step.strategy()).thenReturn(Optional.of(strategyDefinition));
        when(step.errors()).thenReturn(of("Error message"));
        strategyUnderTest.execute(createScenarioExecution(null), step, new ScenarioContextImpl(), null);

        verify(step, times(3)).execute(any(), any(), any());
        verify(step).addErrorMessage(eq("Error(s) on last step execution:"));
        verify(step).addErrorMessage(eq("Error message"));
    }

    @Test
    public void should_execute_all_actions_when_status_is_not_KO() {
        Step rootStep = mock(Step.class);

        StepExecutionStrategies strategies = mock(StepExecutionStrategies.class);

        List<StepExecutionStrategy> strategiesMock = Lists.newArrayList();

        List<Step> mockSteps = Arrays.stream(Status.values())
            .filter(s -> !FAILURE.equals(s))
            .map(status -> {
                Step step = mock(Step.class);
                StepStrategyDefinition sd = mock(StepStrategyDefinition.class);
                when(step.strategy()).thenReturn(Optional.of(sd));
                StepExecutionStrategy strategy = mockStrategy(status);
                when(strategies.buildStrategyFrom(step)).thenReturn(strategy);
                strategiesMock.add(strategy);
                return step;
            })
            .collect(Collectors.toList());

        when(rootStep.subSteps()).thenReturn(mockSteps);
        when(rootStep.isParentStep()).thenReturn(true);
        when(rootStep.dataEvaluator()).thenReturn(new StepDataEvaluator(null));

        RetryWithTimeOutStrategy strategy = new RetryWithTimeOutStrategy();

        StrategyProperties properties = properties("2 s", "5 ms");
        StepStrategyDefinition strategyDefinition = new StepStrategyDefinition("", properties);
        when(rootStep.strategy()).thenReturn(Optional.of(strategyDefinition));

        strategy.execute(createScenarioExecution(null), rootStep, new ScenarioContextImpl(), strategies);

        strategiesMock.forEach(strat -> verify(strat, times(1)).execute(any(), any(), any(), any(), any()));
    }

    @ParameterizedTest
    @MethodSource("retryStrategyProperties")
    public void steps_container_retry_until_all_steps_in_success(StrategyProperties retryProperties, Map<String, Object> context) {
        String timeOut = retryProperties.getProperty("timeOut", String.class);
        String retryDelay = retryProperties.getProperty("retryDelay", String.class);

        Step rootStep = mock(Step.class);

        Step step1 = mock(Step.class);
        StepStrategyDefinition sd1 = mock(StepStrategyDefinition.class);
        when(step1.strategy()).thenReturn(Optional.of(sd1));
        Step step2 = mock(Step.class);
        StepStrategyDefinition sd2 = mock(StepStrategyDefinition.class);
        when(step2.strategy()).thenReturn(Optional.of(sd2));
        Step step3 = mock(Step.class);
        StepStrategyDefinition sd3 = mock(StepStrategyDefinition.class);
        when(step3.strategy()).thenReturn(Optional.of(sd3));

        StepExecutionStrategy strategy1 = mockStrategy(FAILURE, FAILURE, FAILURE, SUCCESS);
        StepExecutionStrategy strategy2 = mockStrategy(SUCCESS);
        StepExecutionStrategy strategy3 = mockStrategy(FAILURE, FAILURE, FAILURE, FAILURE, FAILURE, FAILURE, SUCCESS);

        when(rootStep.subSteps()).thenReturn(Lists.newArrayList(step1, step2, step3));
        when(rootStep.isParentStep()).thenReturn(true);
        when(rootStep.dataEvaluator()).thenReturn(new StepDataEvaluator(null));

        StepExecutionStrategies strategies = mock(StepExecutionStrategies.class);
        when(strategies.buildStrategyFrom(step1)).thenReturn(strategy1);
        when(strategies.buildStrategyFrom(step2)).thenReturn(strategy2);
        when(strategies.buildStrategyFrom(step3)).thenReturn(strategy3);

        StepStrategyDefinition strategyDefinition = new StepStrategyDefinition("", retryProperties);
        when(rootStep.strategy()).thenReturn(Optional.of(strategyDefinition));

        ScenarioContextImpl scenarioContext = new ScenarioContextImpl();
        scenarioContext.putAll(context);
        strategyUnderTest.execute(createScenarioExecution(null), rootStep, scenarioContext, strategies);

        verify(strategy1, times(10)).execute(any(), eq(step1), any(), any(), eq(strategies));
        verify(strategy2, times(7)).execute(any(), eq(step2), any(), any(), eq(strategies));
        verify(strategy3, times(7)).execute(any(), eq(step3), any(), any(), eq(strategies));

        ArgumentCaptor<String> infoCaptor = ArgumentCaptor.forClass(String.class);
        verify(rootStep, atLeastOnce()).addInformation(infoCaptor.capture());

        List<String> capturedInfos = infoCaptor.getAllValues();

        String expectedTimeOut = timeOut.startsWith("${") ? "10 s" : timeOut;
        String expectedRetryDelay = retryDelay.startsWith("${") ? "150 ms" : retryDelay;
        String expectedInfo = String.format("Retry strategy definition : [timeOut %s] [delay %s]", expectedTimeOut, expectedRetryDelay);
        assertThat(capturedInfos).anyMatch(info -> info.contains(expectedInfo));
    }

    @Test
    public void should_resolve_name_from_context_with_strategy_retry() {
        // G
        final TestEngine testEngine = new ExecutionConfiguration().embeddedTestEngine();
        ExecutionRequestDto requestDto = Jsons.loadJsonFromClasspath("scenarios_examples/retryStrategy/retry_strategy_step_with_name_resolver_from_context_put.json", ExecutionRequestDto.class);

        // W
        StepExecutionReportDto result = testEngine.execute(requestDto);

        // T
        assertThat(result).hasFieldOrPropertyWithValue("status", StatusDto.SUCCESS);
        assertThat(result.steps).hasSize(2);
        assertThat(result.steps.get(1).name).isEqualTo("Step 2 Parent : value");
    }


    @ParameterizedTest
    @MethodSource("retryStepStatus")
    public void should_reset_step_execution_before_each_retry(List<Status> stepStatus) {
        StrategyProperties strategyProperties = properties("1 sec", "5 ms");
        StepStrategyDefinition strategyDefinition = new StepStrategyDefinition("", strategyProperties);
        Step step = mockStep(stepStatus.toArray(new Status[0]));
        when(step.strategy()).thenReturn(Optional.of(strategyDefinition));

        strategyUnderTest.execute(createScenarioExecution(null), step, new ScenarioContextImpl(), null);

        verify(step, times(stepStatus.size() - 1)).resetExecution();
    }

    @ParameterizedTest
    @MethodSource("informationParameters")
    public void should_add_information_about_strategy_and_retries(List<Status> stepStatus, StrategyProperties retryProperties, Map<String, Object> context) {
        StepStrategyDefinition strategyDefinition = new StepStrategyDefinition("", retryProperties);
        Step step = mockStep(stepStatus.toArray(new Status[0]));
        when(step.strategy()).thenReturn(Optional.of(strategyDefinition));
        when(step.dataEvaluator()).thenReturn(new StepDataEvaluator(null));

        ScenarioContextImpl scenarioContext = new ScenarioContextImpl();
        scenarioContext.putAll(context);
        strategyUnderTest.execute(createScenarioExecution(null), step, scenarioContext, null);

        String timeOut = retryProperties.getProperty("timeOut", String.class);
        String expectedTimeout = context.getOrDefault("timeOut", timeOut).toString();
        String retryDelay = retryProperties.getProperty("retryDelay", String.class);
        String expectedRetryDelay = context.getOrDefault("retryDelay", retryDelay).toString();
        verify(step, times(2 * stepStatus.size()))
            .addInformation(
                or(
                    and(
                        contains(expectedTimeout),
                        contains(expectedRetryDelay)
                    ),
                    contains("Try number")
                ));
    }

    @Test
    void should_throw_evaluation_exception_when_strategy_properties_not_evaluable() {
        StrategyProperties strategyProperties = new StrategyProperties();
        strategyProperties.setProperty("timeOut", "${#timeOut}");
        strategyProperties.setProperty("retryDelay", "${#retryDelay}");
        StepStrategyDefinition strategyDefinition = new StepStrategyDefinition("", strategyProperties);

        Step step = mock(Step.class);
        when(step.strategy()).thenReturn(Optional.of(strategyDefinition));
        when(step.dataEvaluator()).thenReturn(new StepDataEvaluator(null));

        assertThatThrownBy(() ->
            strategyUnderTest.execute(ScenarioExecution.createScenarioExecution(null), step, new ScenarioContextImpl(), null)
        ).isInstanceOf(EvaluationException.class);
    }

    private StepExecutionStrategy mockStrategy(Status... expectedStatus) {
        StepExecutionStrategy strategyMock = mock(StepExecutionStrategy.class);
        OngoingStubbing<Status> stub = when(strategyMock.execute(any(), any(), any(), any(), any()));

        for (Status st : expectedStatus) {
            stub = stub.thenReturn(st);
        }

        return strategyMock;
    }

    private Step mockStep(Status... expectedStatus) {
        Step stepMock = mock(Step.class);
        OngoingStubbing<Status> stub = when(stepMock.execute(any(), any(), any()));
        for (Status st : expectedStatus) {
            stub = stub.thenReturn(st);
        }

        when(stepMock.dataEvaluator()).thenReturn(new StepDataEvaluator(null));

        return stepMock;
    }


    private void stopExecution(ScenarioExecution scenarioExecution) {
        Field stopField = ReflectionUtils.findField(ScenarioExecution.class, "stop");
        stopField.setAccessible(true);
        ReflectionUtils.setField(stopField, scenarioExecution, true);
    }

    private static Stream<Arguments> informationParameters() {
        return zipStream(retryStepStatus(), retryStrategyProperties());
    }

    private static Stream<Arguments> retryStepStatus() {
        return Stream.of(
            Arguments.of(List.of(SUCCESS)),
            Arguments.of(List.of(FAILURE, SUCCESS)),
            Arguments.of(List.of(FAILURE, FAILURE, SUCCESS)),
            Arguments.of(List.of(FAILURE, FAILURE, FAILURE, SUCCESS))
        );
    }


    private static Stream<Arguments> retryStrategyProperties() {
        return Stream.of(
            Arguments.of(properties("1 min", "5 ms"), emptyMap()),
            Arguments.of(properties("${#timeOut} s", "${#retryDelay}"), Map.of("timeOut", 10L, "retryDelay", "150 ms"))
        );
    }

    private static Stream<Arguments> zipStream(Stream<Arguments> a, Stream<Arguments> b) {
        var aList = a.toList();
        var bList = b.toList();

        var biggestList = aList.size() >= bList.size() ? aList : bList;
        var smallestList = aList == biggestList ? bList : aList;
        int smallestSize = smallestList.size();
        var resultList = new ArrayList<Arguments>();
        for (int i = 0; i < biggestList.size(); i++) {
            Arguments bObjects = biggestList.get(i);
            Arguments sObjects = smallestList.get(i % smallestSize);
            var zipArgs = new ArrayList<>();
            zipArgs.addAll(Arrays.stream(bObjects.get()).toList());
            zipArgs.addAll(Arrays.stream(sObjects.get()).toList());
            resultList.add(Arguments.of(zipArgs.toArray()));
        }

        return resultList.stream();
    }
}

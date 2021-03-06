package com.chutneytesting.design.domain.campaign;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Campaign {

    public final Long id;
    public final String title;
    public final String description;
    public final List<String> scenarioIds;
    public final Map<String, String> executionParameters;
    public final boolean parallelRun;
    public final boolean retryAuto;
    public final String externalDatasetId;
    public final List<String> tags;

    private final LocalTime scheduleTime;
    private String environment;

    public static final DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .appendPattern("HH")
        .appendLiteral(":")
        .appendPattern("mm")
        .toFormatter();

    public Campaign(Long id,
                    String title,
                    String description,
                    List<String> scenarioIds,
                    Map<String, String> executionParameters,
                    LocalTime scheduleTime,
                    String environment,
                    boolean parallelRun,
                    boolean retryAuto,
                    String externalDatasetId,
                    List<String> tags) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.scenarioIds = initListNullOrEmpty(scenarioIds);
        this.executionParameters = Optional.ofNullable(executionParameters).orElse(new HashMap<>());
        this.parallelRun = parallelRun;
        this.retryAuto = retryAuto;
        this.scheduleTime = scheduleTime;
        this.environment = environment;
        this.externalDatasetId = externalDatasetId;
        this.tags = tags;
    }

    public void addScenario(String scenarioId) {
        scenarioIds.add(scenarioId);
    }

    public LocalTime getScheduleTime() {
        return scheduleTime;
    }

    public String getStringScheduleTime() {
        return scheduleTime != null ? scheduleTime.format(formatter) : null;
    }

    public void executionEnvironment(String environment) {
        this.environment = environment;
    }

    public String executionEnvironment() {
        return this.environment;
    }

    private <T> List<T> initListNullOrEmpty(List<T> list) {
        if (list != null && !list.isEmpty()) {
            return list;
        }
        return new ArrayList<>();
    }
}

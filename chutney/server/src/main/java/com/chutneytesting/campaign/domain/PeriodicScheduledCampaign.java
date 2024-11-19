/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PeriodicScheduledCampaign {

    public final Long id;
    public final LocalDateTime nextExecutionDate;
    public final Frequency frequency;
    public final String environment;
    public final List<CampaignExecutionRequest> campaignExecutionRequests;

    public PeriodicScheduledCampaign(Long id, LocalDateTime nextExecutionDate, Frequency frequency, String environment, List<CampaignExecutionRequest> campaignExecutionRequests) {
        this.id = id;
        this.nextExecutionDate = nextExecutionDate;
        this.frequency = frequency;
        this.environment = environment;
        this.campaignExecutionRequests = campaignExecutionRequests;
    }

    public PeriodicScheduledCampaign nextScheduledExecution() {
        LocalDateTime scheduledDate = switch (this.frequency) {
            case HOURLY -> this.nextExecutionDate.plusHours(1);
            case DAILY -> this.nextExecutionDate.plusDays(1);
            case WEEKLY -> this.nextExecutionDate.plusWeeks(1);
            case MONTHLY -> this.nextExecutionDate.plusMonths(1);
            default -> throw new IllegalStateException("Unexpected value: " + this.frequency);
        };
        return new PeriodicScheduledCampaign(id, scheduledDate, frequency, environment, campaignExecutionRequests);
    }

    public record CampaignExecutionRequest(Long campaignId, String campaignTitle, String datasetId) {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PeriodicScheduledCampaign that = (PeriodicScheduledCampaign) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(nextExecutionDate, that.nextExecutionDate) &&
            Objects.equals(frequency, that.frequency) &&
            Objects.equals(environment, that.environment) &&
            Objects.equals(campaignExecutionRequests, that.campaignExecutionRequests);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nextExecutionDate, frequency, environment, campaignExecutionRequests);
    }

    @Override
    public String toString() {
        return "SchedulingCampaign{" +
            "id=" + id +
            ", schedulingDate=" + nextExecutionDate +
            ", frequency='" + frequency + '\'' +
            ", environment='" + environment + '\'' +
            ", campaignExecutionRequests='" + campaignExecutionRequests + '\'' +
            '}';
    }
}

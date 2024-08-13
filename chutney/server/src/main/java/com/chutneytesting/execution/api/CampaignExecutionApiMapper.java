/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.api;

import com.chutneytesting.server.core.domain.execution.report.ServerReportStatus;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import java.util.Optional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface CampaignExecutionApiMapper {
    @Mapping(target = "status", source = ".", qualifiedByName = "mapStatus")
    CampaignExecutionReportSummaryDto toCampaignExecutionReportSummaryDto(CampaignExecution campaignExecution);

    default String mapOptionalString(Optional<String> value) {
        return value.orElse(null);
    }

    @Named("mapStatus")
    default ServerReportStatus mapStatus(CampaignExecution campaignExecution) {
        return campaignExecution.status();
    }
}

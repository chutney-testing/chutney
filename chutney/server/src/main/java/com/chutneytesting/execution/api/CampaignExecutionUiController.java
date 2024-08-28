/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.execution.api;

import static com.chutneytesting.campaign.api.dto.CampaignExecutionReportMapper.toDto;

import com.chutneytesting.campaign.api.dto.CampaignExecutionReportDto;
import com.chutneytesting.campaign.api.dto.CampaignExecutionReportMapper;
import com.chutneytesting.dataset.api.ExternalDatasetDto;
import com.chutneytesting.dataset.api.KeyValue;
import com.chutneytesting.execution.api.report.surefire.SurefireCampaignExecutionReportBuilder;
import com.chutneytesting.execution.api.report.surefire.SurefireScenarioExecutionReportBuilder;
import com.chutneytesting.execution.domain.campaign.CampaignExecutionEngine;
import com.chutneytesting.security.infra.SpringUserService;
import com.chutneytesting.server.core.domain.dataset.DataSet;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(CampaignExecutionUiController.BASE_URL)
@CrossOrigin(origins = "*")
public class CampaignExecutionUiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CampaignExecutionUiController.class);
    static final String BASE_URL = "/api/ui/campaign/execution/v1";

    private final CampaignExecutionEngine campaignExecutionEngine;
    private final SurefireCampaignExecutionReportBuilder surefireCampaignExecutionReportBuilder;
    private final SpringUserService userService;
    private final CampaignExecutionApiMapper campaignExecutionApiMapper;

    public CampaignExecutionUiController(
        CampaignExecutionEngine campaignExecutionEngine,
        SurefireScenarioExecutionReportBuilder surefireScenarioExecutionReportBuilder,
        SpringUserService userService,
        CampaignExecutionApiMapper campaignExecutionApiMapper) {
        this.campaignExecutionEngine = campaignExecutionEngine;
        this.surefireCampaignExecutionReportBuilder = new SurefireCampaignExecutionReportBuilder(surefireScenarioExecutionReportBuilder);
        this.userService = userService;
        this.campaignExecutionApiMapper = campaignExecutionApiMapper;
    }


    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @GetMapping(path = {"/{campaignName}/lastExecution", "/{campaignName}/{env}/lastExecution"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public CampaignExecutionReportSummaryDto getLastCampaignExecution(@PathVariable("campaignId") Long campaignId) {
        CampaignExecution lastCampaignExecution = campaignExecutionEngine.getLastCampaignExecution(campaignId);
        return campaignExecutionApiMapper.toCampaignExecutionReportSummaryDto(lastCampaignExecution);
    }


    @PreAuthorize("hasAuthority('CAMPAIGN_EXECUTE')")
    @GetMapping(path = {"/{campaignName}", "/{campaignName}/{env}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CampaignExecutionReportDto> executeCampaignByName(@PathVariable("campaignName") String campaignName, @PathVariable("env") Optional<String> environment) {
        List<CampaignExecution> reports;
        String userId = userService.currentUser().getId();
        if (environment.isPresent()) {
            reports = campaignExecutionEngine.executeByNameWithEnv(campaignName, environment.get(), userId);
        } else {
            reports = campaignExecutionEngine.executeByName(campaignName, userId);
        }
        return reports.stream()
            .map(CampaignExecutionReportMapper::toDto)
            .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_EXECUTE')")
    @PostMapping(path = {"/replay/{campaignExecutionId}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public CampaignExecutionReportDto replayFailedScenario(@PathVariable("campaignExecutionId") Long campaignExecutionId) {
        String userId = userService.currentUser().getId();
        CampaignExecution newExecution = campaignExecutionEngine.replayFailedScenariosExecutionsForExecution(campaignExecutionId, userId);
        return toDto(newExecution);
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_EXECUTE')")
    @GetMapping(path = {"/{campaignPattern}/surefire", "/{campaignPattern}/surefire/{env}"}, produces = "application/zip")
    public byte[] executeCampaignsByPatternWithSurefireReport(HttpServletResponse response, @PathVariable("campaignPattern") String campaignPattern, @PathVariable("env") Optional<String> environment) {
        String userId = userService.currentUser().getId();
        response.addHeader("Content-Disposition", "attachment; filename=\"surefire-report.zip\"");
        List<CampaignExecution> reports;
        if (environment.isPresent()) {
            reports = campaignExecutionEngine.executeByNameWithEnv(campaignPattern, environment.get(), userId);
        } else {
            reports = campaignExecutionEngine.executeByName(campaignPattern, userId);
        }
        return surefireCampaignExecutionReportBuilder.createReport(reports);
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_EXECUTE')")
    @PostMapping(path = "/{executionId}/stop")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void stopExecution(@PathVariable("executionId") Long executionId) {
        LOGGER.debug("Stop campaign execution {}", executionId);
        campaignExecutionEngine.stopExecution(executionId);
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_EXECUTE')")
    @PostMapping(path = {"/byID/{campaignId}", "/byID/{campaignId}/{env}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public CampaignExecutionReportDto executeCampaignById(
            @PathVariable("campaignId") Long campaignId,
            @PathVariable("env") Optional<String> environment,
            @RequestBody ExternalDatasetDto dataset
    ) {
        String userId = userService.currentUser().getId();
        CampaignExecution report;
        DataSet ds;
        if (dataset == null) {
            ds = null;
        } else {
            ds = DataSet.builder()
                .withId(dataset.datasetId().orElse(null))
                .withName(dataset.datasetId().orElse(""))
                .withConstants(KeyValue.toMap(dataset.constants()))
                .withDatatable(dataset.datatable().stream().map(KeyValue::toMap).toList())
                .build();
        }
        report = campaignExecutionEngine.executeByIdWithEnvAndDataset(campaignId, environment.orElse(null), ds, userId);
        return toDto(report);
    }
}

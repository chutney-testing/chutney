/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.api;

import static com.chutneytesting.campaign.api.CampaignController.BASE_URL;
import static com.chutneytesting.campaign.api.dto.CampaignMapper.fromDto;
import static com.chutneytesting.campaign.api.dto.CampaignMapper.toDto;
import static com.chutneytesting.campaign.api.dto.CampaignMapper.toDtoWithoutReport;
import static java.util.Optional.ofNullable;

import com.chutneytesting.campaign.api.dto.CampaignDto;
import com.chutneytesting.campaign.api.dto.CampaignExecutionFullReportDto;
import com.chutneytesting.campaign.api.dto.CampaignExecutionReportDto;
import com.chutneytesting.campaign.api.dto.CampaignExecutionReportMapper;
import com.chutneytesting.campaign.api.dto.CampaignMapper;
import com.chutneytesting.campaign.api.dto.ExternalDatasetDto;
import com.chutneytesting.campaign.domain.CampaignExecutionRepository;
import com.chutneytesting.campaign.domain.CampaignRepository;
import com.chutneytesting.campaign.domain.CampaignService;
import com.chutneytesting.dataset.domain.DatasetService;
import com.chutneytesting.scenario.api.raw.dto.TestCaseIndexDto;
import com.chutneytesting.scenario.infra.TestCaseRepositoryAggregator;
import com.chutneytesting.server.core.domain.dataset.DataSetAlreadyExistException;
import com.chutneytesting.server.core.domain.dataset.InvalidExternalDatasetException;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistory;
import com.chutneytesting.server.core.domain.execution.history.ExecutionHistoryRepository;
import com.chutneytesting.server.core.domain.scenario.ScenarioNotFoundException;
import com.chutneytesting.server.core.domain.scenario.campaign.Campaign;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignExecution;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(BASE_URL)
@CrossOrigin(origins = "*")
public class CampaignController {

    public static final String BASE_URL = "/api/ui/campaign/v1";

    private final TestCaseRepositoryAggregator repositoryAggregator;
    private final CampaignRepository campaignRepository;
    private final DatasetService datasetService;
    private final CampaignExecutionRepository campaignExecutionRepository;
    private final ExecutionHistoryRepository executionHistoryRepository;
    private final CampaignService campaignService;

    public CampaignController(TestCaseRepositoryAggregator repositoryAggregator,
                              CampaignRepository campaignRepository,
                              DatasetService datasetService, CampaignExecutionRepository campaignExecutionRepository,
                              ExecutionHistoryRepository executionHistoryRepository,
                              CampaignService campaignService) {

        this.repositoryAggregator = repositoryAggregator;
        this.campaignRepository = campaignRepository;
        this.datasetService = datasetService;
        this.campaignExecutionRepository = campaignExecutionRepository;
        this.executionHistoryRepository = executionHistoryRepository;
        this.campaignService = campaignService;
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_WRITE')")
    @PostMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CampaignDto saveCampaign(@RequestBody CampaignDto campaign) {
        hasEnvironment(campaign);
        validateDataset(campaign);
        return toDtoWithoutReport(campaignRepository.createOrUpdate(fromDto(campaign)));
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_WRITE')")
    @PutMapping(path = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public CampaignDto updateCampaign(@RequestBody CampaignDto campaign) {
        hasEnvironment(campaign);
        validateDataset(campaign);
        return toDtoWithoutReport(campaignRepository.createOrUpdate(fromDto(campaign)));
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_WRITE')")
    @DeleteMapping(path = "/{campaignId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteCampaign(@PathVariable("campaignId") Long campaignId) {
        return campaignRepository.removeById(campaignId);
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @GetMapping(path = "/{campaignId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CampaignDto getCampaignById(@PathVariable("campaignId") Long campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId);
        List<CampaignExecution> reports = campaignService.findExecutionsById(campaignId);
        return toDto(campaign, reports);
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @GetMapping(path = "/execution/{campaignExecutionId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CampaignExecutionFullReportDto getCampaignExecutionReportById(@PathVariable("campaignExecutionId") Long campaignExecutionId) {
        CampaignExecution campaignExecution = campaignExecutionRepository.getCampaignExecutionById(campaignExecutionId);

        List<ExecutionHistory.Execution> executions = campaignExecution.scenarioExecutionReports().stream()
            .map(ser -> executionHistoryRepository.getExecution(ser.scenarioId(), ser.execution().executionId()))
            .toList();

        return CampaignExecutionReportMapper.fullExecutionToDto(campaignExecution, executions);
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @GetMapping(path = "/{campaignId}/scenarios", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<TestCaseIndexDto> getCampaignScenarios(@PathVariable("campaignId") Long campaignId) {
        return campaignRepository.findScenariosIds(campaignId).stream()
            .map(id -> repositoryAggregator.findMetadataById(id).orElseThrow(() -> new ScenarioNotFoundException(id)))
            .map(TestCaseIndexDto::from)
            .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CampaignDto> getAllCampaigns() {
        return campaignRepository.findAll().stream()
            .map(CampaignMapper::toDtoWithoutReport)
            .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('CAMPAIGN_READ')")
    @GetMapping(path = "/lastexecutions/{limit}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CampaignExecutionReportDto> getLastExecutions(@PathVariable("limit") Long limit) {
        List<CampaignExecution> lastExecutions = campaignExecutionRepository.getLastExecutions(limit);

        return lastExecutions.stream()
            .map(CampaignExecutionReportMapper::toDto)
            .sorted(Comparator.comparing(value -> ((CampaignExecutionReportDto) value).getStartDate()).reversed())
            .collect(Collectors.toList());
    }

    @PreAuthorize("hasAuthority('SCENARIO_READ')")
    @GetMapping(path = "/scenario/{scenarioId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<CampaignDto> getCampaignsByScenarioId(@PathVariable("scenarioId") String scenarioId) {
        return campaignRepository.findCampaignsByScenarioId(scenarioId).stream()
            .map(CampaignMapper::toDtoWithoutReport)
            .collect(Collectors.toList());
    }

    private void hasEnvironment(CampaignDto campaign) {
        if (StringUtils.isBlank(campaign.getEnvironment())) {
            throw new IllegalArgumentException("Environment is missing for campaign with name " + campaign.getTitle());
        }
    }

    private void validateDataset(CampaignDto campaign) {
        validateExternalDataset(campaign.getDataset());
        campaign.getScenarios()
            .stream()
            .map(scenario -> ofNullable(scenario.dataset()).map(ExternalDatasetDto::getDatasetId).orElse(null))
            .filter(StringUtils::isNotBlank)
            .distinct()
            .forEach(datasetService::findById);
    }

    private void validateExternalDataset(ExternalDatasetDto externalDatasetDto) {
        if (externalDatasetDto == null) {
            return;
        }
        boolean datasetIdIsPresent = externalDatasetDto.getDatasetId() != null;
        boolean constantsArePresent = ofNullable(externalDatasetDto.getConstants()).map(constants -> !constants.keySet().isEmpty()).orElse(false);
        boolean datatableIsPresent = ofNullable(externalDatasetDto.getDatatable()).map(constants -> !constants.isEmpty()).orElse(false);
        if (datasetIdIsPresent && (constantsArePresent || datatableIsPresent)) {
            throw new InvalidExternalDatasetException(externalDatasetDto.getDatasetId());
        }
        if (!(datasetIdIsPresent || constantsArePresent || datatableIsPresent))
        {
            throw new InvalidExternalDatasetException();
        }
    }
}

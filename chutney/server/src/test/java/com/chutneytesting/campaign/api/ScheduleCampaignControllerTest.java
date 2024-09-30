/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.chutneytesting.RestExceptionHandler;
import com.chutneytesting.campaign.domain.Frequency;
import com.chutneytesting.campaign.domain.PeriodicScheduledCampaign;
import com.chutneytesting.campaign.domain.PeriodicScheduledCampaign.CampaignExecutionRequest;
import com.chutneytesting.campaign.domain.ScheduledCampaignRepository;
import com.chutneytesting.server.core.domain.instrument.ChutneyMetrics;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class ScheduleCampaignControllerTest {

    private MockMvc mockMvc;

    private final ScheduledCampaignRepository scheduledCampaignRepository = mock(ScheduledCampaignRepository.class);

    private final ScheduleCampaignController scheduleCampaignController = new ScheduleCampaignController(scheduledCampaignRepository);

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(scheduleCampaignController)
            .setControllerAdvice(new RestExceptionHandler(Mockito.mock(ChutneyMetrics.class)))
            .build();
    }

    @Test
    @WithMockUser(authorities = "CAMPAIGN_READ")
    void should_get_all_scheduled_campaigns() throws Exception {

        CampaignExecutionRequest request = new CampaignExecutionRequest(1L, "title", "datasetId");
        when(scheduledCampaignRepository.getAll()).thenReturn(List.of(
            new PeriodicScheduledCampaign(1L,  LocalDateTime.of(2024, 10, 12, 14, 30, 45), Frequency.DAILY, "PROD", List.of(request))
        ));

        // Act & Assert
        mockMvc.perform(get("/api/ui/campaign/v1/scheduling")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(1L))
            .andExpect(jsonPath("$[0].frequency").value("Daily"))
            .andExpect(jsonPath("$[0].environment").value("PROD"))
            .andExpect(jsonPath("$[0].campaignExecutionRequest[0].campaignId").value(1L))
            .andExpect(jsonPath("$[0].campaignExecutionRequest[0].campaignTitle").value("title"))
            .andExpect(jsonPath("$[0].campaignExecutionRequest[0].datasetId").value("datasetId"))
            .andExpect(jsonPath("$[0].schedulingDate[0]").value(2024))
            .andExpect(jsonPath("$[0].schedulingDate[1]").value(10))
            .andExpect(jsonPath("$[0].schedulingDate[2]").value(12))
            .andExpect(jsonPath("$[0].schedulingDate[3]").value(14))
            .andExpect(jsonPath("$[0].schedulingDate[4]").value(30))
            .andExpect(jsonPath("$[0].schedulingDate[5]").value(45));

        verify(scheduledCampaignRepository, times(1)).getAll();
    }

    @Test
    @WithMockUser(authorities = "CAMPAIGN_WRITE")
    void should_add_scheduled_campaign() throws Exception {

        mockMvc.perform(post("/api/ui/campaign/v1/scheduling")
            .contentType(MediaType.APPLICATION_JSON)
            .content(
                """
                    {
                        "id":1,
                        "schedulingDate":[2024,10,12,14,30,45],
                        "frequency":"Daily",
                        "environment":"PROD",
                        "campaignExecutionRequest":[
                            {"campaignId":1,"campaignTitle":"title","datasetId":"datasetId"}
                        ]
                    }
                """))
            .andExpect(status().isOk());

        ArgumentCaptor<PeriodicScheduledCampaign> captor = ArgumentCaptor.forClass(PeriodicScheduledCampaign.class);


        verify(scheduledCampaignRepository).add(captor.capture());

        PeriodicScheduledCampaign capturedCampaign = captor.getValue();

        assertThat(capturedCampaign)
            .isNotNull()
            .extracting("id", "nextExecutionDate", "frequency.label", "environment")
            .containsExactly(
                1L,
                LocalDateTime.of(2024, 10, 12, 14, 30, 45),
                "Daily",
                "PROD"
            );

        assertThat(capturedCampaign.campaignExecutionRequests)
            .hasSize(1)
            .first()
            .extracting("campaignId", "campaignTitle", "datasetId")
            .containsExactly(1L, "title", "datasetId");
    }

    @Test
    @WithMockUser(authorities = "CAMPAIGN_WRITE")
    void should_delete_scheduled_campaign() throws Exception {
        Long schedulingCampaignId = 1L;

        mockMvc.perform(delete("/api/ui/campaign/v1/scheduling/{schedulingCampaignId}", schedulingCampaignId)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(scheduledCampaignRepository).removeById(schedulingCampaignId);
    }
}

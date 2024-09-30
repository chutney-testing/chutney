/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.infra;


import static java.time.LocalDateTime.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.chutneytesting.campaign.domain.Frequency;
import com.chutneytesting.campaign.domain.PeriodicScheduledCampaign;
import com.chutneytesting.campaign.domain.PeriodicScheduledCampaign.CampaignExecutionRequest;
import com.chutneytesting.campaign.domain.ScheduledCampaignRepository;
import com.chutneytesting.tools.file.FileUtils;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ScheduledCampaignFileRepositoryTest {

    private static ScheduledCampaignRepository sut;
    private static Path SCHEDULING_CAMPAIGN_FILE;
    @TempDir
    private static Path temporaryFolder;

    @BeforeEach
    void setup() {
        String tmpConfDir = temporaryFolder.toFile().getAbsolutePath();
        SCHEDULING_CAMPAIGN_FILE = Paths.get(tmpConfDir + "/scheduling/schedulingCampaigns.json");
        sut = new SchedulingCampaignFileRepository(tmpConfDir);
        FileUtils.writeContent(SCHEDULING_CAMPAIGN_FILE, "{}");
    }

    @Test
    void add_get_and_remove_scheduled_campaign() {
        //// ADD
        // Given
        PeriodicScheduledCampaign sc1 = create(11L, "campaign title 1", of(2020, 2, 4, 7, 10));
        PeriodicScheduledCampaign sc2 = create(22L, "campaign title 2", of(2021, 3, 5, 8, 11));
        PeriodicScheduledCampaign sc3 = create(33L, "campaign title 3", of(2022, 4, 6, 9, 12));
        PeriodicScheduledCampaign sc4 = create(null, List.of(55L, 66L), List.of("campaign title 5", "campaign title 6"), of(2022, 4, 6, 9, 12), null, null);
        String expectedAdded =
            """
                {
                  "1" : {
                    "id" : "1",
                    "schedulingDate" : [ 2020, 2, 4, 7, 10 ],
                    "campaignsId" : [ 11 ],
                    "campaignsTitle" : [ "campaign title 1" ],
                    "datasetsId" : [ "" ]
                  },
                  "2" : {
                    "id" : "2",
                    "schedulingDate" : [ 2021, 3, 5, 8, 11 ],
                    "campaignsId" : [ 22 ],
                    "campaignsTitle" : [ "campaign title 2" ],
                    "datasetsId" : [ "" ]
                  },
                  "3" : {
                    "id" : "3",
                    "schedulingDate" : [ 2022, 4, 6, 9, 12 ],
                    "campaignsId" : [ 33 ],
                    "campaignsTitle" : [ "campaign title 3" ],
                    "datasetsId" : [ "" ]
                  },
                  "4" : {
                    "id" : "4",
                    "schedulingDate" : [ 2022, 4, 6, 9, 12 ],
                    "campaignsId" : [ 55, 66 ],
                    "campaignsTitle" : [ "campaign title 5", "campaign title 6" ],
                    "datasetsId" : [ "", "" ]
                  }
                }
                """;

        // When
        sut.add(sc1);
        sut.add(sc2);
        sut.add(sc3);
        sut.add(sc4);

        // Then
        String actualContent = FileUtils.readContent(SCHEDULING_CAMPAIGN_FILE);
        assertThat(actualContent).isEqualToIgnoringNewLines(expectedAdded);

        //// REMOVE
        // Given
        String expectedAfterRemove =
            """
                {
                   "1" : {
                     "id" : "1",
                     "schedulingDate" : [ 2020, 2, 4, 7, 10 ],
                     "campaignsId" : [ 11 ],
                     "campaignsTitle" : [ "campaign title 1" ],
                     "datasetsId" : [ "" ]
                   },
                   "3" : {
                     "id" : "3",
                     "schedulingDate" : [ 2022, 4, 6, 9, 12 ],
                     "campaignsId" : [ 33 ],
                     "campaignsTitle" : [ "campaign title 3" ],
                     "datasetsId" : [ "" ]
                   }
                 }
            """;

        // When
        sut.removeById(2L);
        sut.removeById(4L);

        // Then
        actualContent = FileUtils.readContent(SCHEDULING_CAMPAIGN_FILE);
        assertThat(actualContent).isEqualToIgnoringWhitespace(expectedAfterRemove);

        //// GET
        // When
        List<PeriodicScheduledCampaign> periodicScheduledCampaigns = sut.getAll();

        // Then
        assertThat(periodicScheduledCampaigns).hasSize(2);
        PeriodicScheduledCampaign sc1WithId = create(1L, 11L, "campaign title 1", of(2020, 2, 4, 7, 10));
        PeriodicScheduledCampaign sc3WithId = create(3L, 33L, "campaign title 3", of(2022, 4, 6, 9, 12));

        assertThat(periodicScheduledCampaigns).contains(sc1WithId, sc3WithId);
    }

    @Test
    void remove_campaign_from_scheduled() {
        // Given
        PeriodicScheduledCampaign periodicScheduledCampaign = create(null, List.of(1L, 2L, 3L), List.of("campaign title 1", "campaign title 2", "campaign title 3"), of(2024, 2, 4, 7, 10), null, null);
        sut.add(periodicScheduledCampaign);

        String expectedAdded =
            """
                {
                  "1" : {
                    "id" : "1",
                    "schedulingDate" : [ 2024, 2, 4, 7, 10 ],
                    "campaignsId" : [ 1, 3 ],
                    "campaignsTitle" : [ "campaign title 1", "campaign title 3" ],
                    "datasetsId" : [ "", "" ]
                  }
                }
                """;

        // When
        sut.removeCampaignId(2L);

        // Then
        String actualContent = FileUtils.readContent(SCHEDULING_CAMPAIGN_FILE);
        assertThat(actualContent).isEqualToIgnoringNewLines(expectedAdded);
    }

    @Test
    void remove_schedule_without_campaign_after_removing_campaign() {
        // Given
        PeriodicScheduledCampaign sc1 = create(11L, "campaign title 1", of(2020, 2, 4, 7, 10));
        PeriodicScheduledCampaign sc2 = create(22L, "campaign title 2", of(2021, 3, 5, 8, 11));
        sut.add(sc1);
        sut.add(sc2);
        String expectedAdded =
            """
                    {
                      "2" : {
                        "id" : "2",
                        "schedulingDate" : [ 2021, 3, 5, 8, 11 ],
                        "campaignsId" : [ 22 ],
                        "campaignsTitle" : [ "campaign title 2" ],
                        "datasetsId" : [ "" ]
                      }
                    }
                """;

        // When
        sut.removeCampaignId(11L);

        // Then
        String actualContent = FileUtils.readContent(SCHEDULING_CAMPAIGN_FILE);
        assertThat(actualContent).isEqualToIgnoringWhitespace(expectedAdded);
    }

    @Test
    void should_read_and_write_scheduled_campaign_concurrently() throws InterruptedException {
        List<Exception> exceptions = new ArrayList<>();
        Runnable addScheduledCampaign = () -> {
            try {
                sut.add(create(11L, "campaign title 1", LocalDateTime.now().minusWeeks(1)));
            } catch (Exception e) {
                exceptions.add(e);
            }
        };
        Runnable readScheduledCampaigns = () -> {
            try {
                sut.getAll();
            } catch (Exception e) {
                exceptions.add(e);
            }
        };

        ExecutorService pool = Executors.newFixedThreadPool(2);
        IntStream.range(1, 5).forEach((i) -> {
            pool.submit(addScheduledCampaign);
            pool.submit(readScheduledCampaigns);
        });
        pool.shutdown();
        if (pool.awaitTermination(1, TimeUnit.SECONDS)) {
            assertThat(exceptions).isEmpty();
        } else {
            fail("Pool termination timeout ...");
        }
    }

    @Test
    public void should_get_and_update_old_scheduled_campaign() {
        //// Get
        // Given
        String old_scheduled_campaign =
            """
                {
                  "1" : {
                    "id" : "1",
                    "campaignsId" : [ 11 ],
                    "campaignsTitle" : [ "campaign title 1" ],
                    "schedulingDate" : [ 2020, 2, 4, 7, 10 ]
                  }
                }
            """;
        FileUtils.writeContent(SCHEDULING_CAMPAIGN_FILE, old_scheduled_campaign);

        PeriodicScheduledCampaign oldSchedule = create(1L, 11L, "campaign title 1", of(2020, 2, 4, 7, 10));
        PeriodicScheduledCampaign newSchedule = create(2L, List.of(22L, 33L, 44L), List.of("campaign title 2", "campaign title 3", "campaign title 4"), of(2021, 3, 5, 8, 11), "MY_ENV", List.of("FIRST_DATASET", "SECOND_DATASET", ""));
        // When
         List<PeriodicScheduledCampaign> periodicScheduledCampaigns = sut.getAll();
        //Then
        assertThat(periodicScheduledCampaigns).hasSize(1);
        assertThat(periodicScheduledCampaigns).contains(oldSchedule);

        //// UPDATE
        // When
        sut.add(newSchedule);

        // Then
        periodicScheduledCampaigns = sut.getAll();
        assertThat(periodicScheduledCampaigns).hasSize(2);
        assertThat(periodicScheduledCampaigns).contains(oldSchedule);
        assertThat(periodicScheduledCampaigns).contains(newSchedule);

        String expectedScheduledCampaignsAfterUpdate =
            """
                {
                  "1" : {
                    "id" : "1",
                    "schedulingDate" : [ 2020, 2, 4, 7, 10 ],
                    "campaignsId" : [ 11 ],
                    "campaignsTitle" : [ "campaign title 1" ],
                    "datasetsId" : [ "" ]
                  },
                  "2" : {
                    "id" : "2",
                    "schedulingDate" : [ 2021, 3, 5, 8, 11 ],
                    "environment" : "MY_ENV",
                    "campaignsId" : [ 22, 33, 44 ],
                    "campaignsTitle" : [ "campaign title 2", "campaign title 3", "campaign title 4" ],
                    "datasetsId" : [ "FIRST_DATASET", "SECOND_DATASET", "" ]
                  }
                }
                """;
        String actualContent = FileUtils.readContent(SCHEDULING_CAMPAIGN_FILE);
        assertThat(actualContent).isEqualToIgnoringWhitespace(expectedScheduledCampaignsAfterUpdate);
    }

    private PeriodicScheduledCampaign create(Long id, Long campaignId, String campaignTitle, LocalDateTime nextExecutionDate) {
        return create(id, List.of(campaignId), List.of(campaignTitle), nextExecutionDate, null, null);
    }

    private PeriodicScheduledCampaign create(Long campaignId, String campaignTitle, LocalDateTime nextExecutionDate) {
        return create(null, campaignId, campaignTitle, nextExecutionDate);
    }

    private PeriodicScheduledCampaign create(Long campaignId, String campaignTitle, LocalDateTime nextExecutionDate, String environment, String datasetId) {
        return create(null, List.of(campaignId), List.of(campaignTitle), nextExecutionDate, environment, List.of(datasetId));
    }

    private PeriodicScheduledCampaign create(Long id, List<Long> campaignsId, List<String> campaignsTitle, LocalDateTime nextExecutionDate, String environment, final List<String> datasetIds) {
        List<CampaignExecutionRequest> campaignExecutionRequests =
            IntStream.range(0, campaignsId.size())
                .mapToObj(i -> {
                    var tmpDatasetIdList = datasetIds;
                    if(datasetIds == null) {
                        tmpDatasetIdList = campaignsId.stream().map(c -> "").toList();
                    }
                    return new CampaignExecutionRequest(
                        campaignsId.get(i),
                        campaignsTitle.get(i),
                        tmpDatasetIdList.get(i));
                })
                .toList();
        return new PeriodicScheduledCampaign(id, nextExecutionDate, Frequency.EMPTY, environment, campaignExecutionRequests);
    }
}

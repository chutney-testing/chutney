/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.infra;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import com.chutneytesting.campaign.domain.PeriodicScheduledCampaign;
import com.chutneytesting.campaign.domain.PeriodicScheduledCampaignRepository;
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

public class PeriodicScheduledCampaignFileRepositoryTest {

    private static PeriodicScheduledCampaignRepository sut;
    private static Path SCHEDULING_CAMPAIGN_FILE;
    @TempDir
    private static Path temporaryFolder;

    @BeforeEach
    public void setup() {
        String tmpConfDir = temporaryFolder.toFile().getAbsolutePath();
        SCHEDULING_CAMPAIGN_FILE = Paths.get(tmpConfDir + "/scheduling/schedulingCampaigns.json");
        sut = new SchedulingCampaignFileRepository(tmpConfDir);
    }

    @Test
    public void should_add_get_and_remove_scheduled_campaign() {
        //// ADD
        // Given
        PeriodicScheduledCampaign sc1 = new PeriodicScheduledCampaign(null, 11L, "campaign title 1", LocalDateTime.of(2020, 2, 4, 7, 10));
        PeriodicScheduledCampaign sc2 = new PeriodicScheduledCampaign(null, 22L, "campaign title 2", LocalDateTime.of(2021, 3, 5, 8, 11));
        PeriodicScheduledCampaign sc3 = new PeriodicScheduledCampaign(null, 33L, "campaign title 3", LocalDateTime.of(2022, 4, 6, 9, 12));
        PeriodicScheduledCampaign sc4 = new PeriodicScheduledCampaign(null, List.of(55L, 66L), List.of("campaign title 5", "campaign title 6"), LocalDateTime.of(2022, 4, 6, 9, 12));
        String expectedAdded =
            """
                {
                  "1" : {
                    "id" : "1",
                    "campaignsId" : [ 11 ],
                    "campaignsTitle" : [ "campaign title 1" ],
                    "schedulingDate" : [ 2020, 2, 4, 7, 10 ]
                  },
                  "2" : {
                    "id" : "2",
                    "campaignsId" : [ 22 ],
                    "campaignsTitle" : [ "campaign title 2" ],
                    "schedulingDate" : [ 2021, 3, 5, 8, 11 ]
                  },
                  "3" : {
                    "id" : "3",
                    "campaignsId" : [ 33 ],
                    "campaignsTitle" : [ "campaign title 3" ],
                    "schedulingDate" : [ 2022, 4, 6, 9, 12 ]
                  },
                  "4" : {
                    "id" : "4",
                    "campaignsId" : [ 55, 66 ],
                    "campaignsTitle" : [ "campaign title 5", "campaign title 6" ],
                    "schedulingDate" : [ 2022, 4, 6, 9, 12 ]
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
                    "campaignsId" : [ 11 ],
                    "campaignsTitle" : [ "campaign title 1" ],
                    "schedulingDate" : [ 2020, 2, 4, 7, 10 ]
                  },
                  "3" : {
                    "id" : "3",
                    "campaignsId" : [ 33 ],
                    "campaignsTitle" : [ "campaign title 3" ],
                    "schedulingDate" : [ 2022, 4, 6, 9, 12 ]
                  }
                }
                """;

        // When
        sut.removeById(2L);
        sut.removeById(4L);

        // Then
        actualContent = FileUtils.readContent(SCHEDULING_CAMPAIGN_FILE);
        assertThat(actualContent).isEqualToIgnoringNewLines(expectedAfterRemove);

        //// GET
        // When
        List<PeriodicScheduledCampaign> periodicScheduledCampaigns = sut.getAll();

        // Then
        assertThat(periodicScheduledCampaigns).hasSize(2);
        PeriodicScheduledCampaign sc1WithId = new PeriodicScheduledCampaign(1L, 11L, "campaign title 1", LocalDateTime.of(2020, 2, 4, 7, 10));
        PeriodicScheduledCampaign sc3WithId = new PeriodicScheduledCampaign(3L, 33L, "campaign title 3", LocalDateTime.of(2022, 4, 6, 9, 12));

        assertThat(periodicScheduledCampaigns).contains(sc1WithId, sc3WithId);
    }

    @Test
    public void should_remove_campaign_from_scheduled() {
        // Given
        FileUtils.writeContent(SCHEDULING_CAMPAIGN_FILE, "{}");
        PeriodicScheduledCampaign periodicScheduledCampaign = new PeriodicScheduledCampaign(null, List.of(1L, 2L, 3L), List.of("campaign title 1","campaign title 2","campaign title 3"), LocalDateTime.of(2024, 2, 4, 7, 10));
        sut.add(periodicScheduledCampaign);

        String expectedAdded =
            """
                {
                  "1" : {
                    "id" : "1",
                    "campaignsId" : [ 1, 3 ],
                    "campaignsTitle" : [ "campaign title 1", "campaign title 3" ],
                    "schedulingDate" : [ 2024, 2, 4, 7, 10 ]
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
    public void should_remove_schedule_without_campaign_after_removing_campaign() {
        // Given
        FileUtils.writeContent(SCHEDULING_CAMPAIGN_FILE, "{}");
        PeriodicScheduledCampaign sc1 = new PeriodicScheduledCampaign(null, 11L, "campaign title 1", LocalDateTime.of(2020, 2, 4, 7, 10));
        PeriodicScheduledCampaign sc2 = new PeriodicScheduledCampaign(null, 22L, "campaign title 2", LocalDateTime.of(2021, 3, 5, 8, 11));
        sut.add(sc1);
        sut.add(sc2);
        String expectedAdded =
            """
                {
                  "2" : {
                    "id" : "2",
                    "campaignsId" : [ 22 ],
                    "campaignsTitle" : [ "campaign title 2" ],
                    "schedulingDate" : [ 2021, 3, 5, 8, 11 ]
                  }
                }
                """;

        // When
        sut.removeCampaignId(11L);

        // Then
        String actualContent = FileUtils.readContent(SCHEDULING_CAMPAIGN_FILE);
        assertThat(actualContent).isEqualToIgnoringNewLines(expectedAdded);
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
                    "campaignId" : 11 ,
                    "campaignTitle" : "campaign title 1",
                    "schedulingDate" : [ 2020, 2, 4, 7, 10 ]
                  }
                }
                """;

        FileUtils.writeContent(SCHEDULING_CAMPAIGN_FILE, old_scheduled_campaign);

        PeriodicScheduledCampaign sc1 = new PeriodicScheduledCampaign(1L, 11L, "campaign title 1", LocalDateTime.of(2020, 2, 4, 7, 10));
        PeriodicScheduledCampaign sc2 = new PeriodicScheduledCampaign(2L, 22L, "campaign title 2", LocalDateTime.of(2023, 3, 4, 7, 10));

        // When
        List<PeriodicScheduledCampaign> periodicScheduledCampaigns = sut.getAll();
        //Then
        assertThat(periodicScheduledCampaigns).hasSize(1);
        assertThat(periodicScheduledCampaigns).contains(sc1);

        //// UPDATE
        // When
        sut.add(sc2);

        // Then
        periodicScheduledCampaigns = sut.getAll();
        assertThat(periodicScheduledCampaigns).hasSize(2);
        assertThat(periodicScheduledCampaigns).contains(sc1);
        assertThat(periodicScheduledCampaigns).contains(sc2);

        String expectedScheduledCampaignsAfterUpdate =
            """
                {
                  "1" : {
                    "id" : "1",
                    "campaignsId" : [ 11 ],
                    "campaignsTitle" : [ "campaign title 1" ],
                    "schedulingDate" : [ 2020, 2, 4, 7, 10 ]
                  },
                  "2" : {
                    "id" : "2",
                    "campaignsId" : [ 22 ],
                    "campaignsTitle" : [ "campaign title 2" ],
                    "schedulingDate" : [ 2023, 3, 4, 7, 10 ]
                  }
                }
                """;
        String actualContent = FileUtils.readContent(SCHEDULING_CAMPAIGN_FILE);
        assertThat(actualContent).isEqualToIgnoringNewLines(expectedScheduledCampaignsAfterUpdate);
    }

    @Test
    void should_read_and_write_concurrently() throws InterruptedException {
        List<Exception> exceptions = new ArrayList<>();
        Runnable addScheduledCampaign = () -> {
            try {
                sut.add(new PeriodicScheduledCampaign(null, 11L, "campaign title 1", LocalDateTime.now().minusWeeks(1)));
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
        if (pool.awaitTermination(5, TimeUnit.SECONDS)) {
            assertThat(exceptions).isEmpty();
        } else {
            fail("Pool termination timeout ...");
        }
    }
}

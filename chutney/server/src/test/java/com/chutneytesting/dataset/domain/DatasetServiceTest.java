/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.dataset.domain;

import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.chutneytesting.campaign.domain.CampaignRepository;
import com.chutneytesting.scenario.domain.gwt.GwtScenario;
import com.chutneytesting.scenario.domain.gwt.GwtTestCase;
import com.chutneytesting.server.core.domain.dataset.DataSet;
import com.chutneytesting.server.core.domain.scenario.AggregatedRepository;
import com.chutneytesting.server.core.domain.scenario.TestCaseMetadataImpl;
import com.chutneytesting.server.core.domain.scenario.campaign.Campaign;
import com.chutneytesting.server.core.domain.scenario.campaign.CampaignBuilder;
import java.util.List;
import org.junit.jupiter.api.Test;

class DatasetServiceTest {

    private final DataSetRepository datasetRepository = mock(DataSetRepository.class);
    private final CampaignRepository campaignRepository = mock(CampaignRepository.class);
    private final AggregatedRepository<GwtTestCase> testCaseRepository = mock(AggregatedRepository.class);

    DatasetService sut = new DatasetService(datasetRepository, campaignRepository, testCaseRepository);

    @Test
    public void should_sort_dataset_by_name() {
        // Given
        DataSet firstDataset = DataSet.builder().withName("A").build();
        DataSet secondDataset = DataSet.builder().withName("B").build();
        DataSet thirdDataset = DataSet.builder().withName("C").build();

        when(datasetRepository.findAll())
            .thenReturn(List.of(thirdDataset, secondDataset, firstDataset));

        // When
        List<DataSet> actual = sut.findAll();

        // Then
        assertThat(actual).containsExactly(firstDataset, secondDataset, thirdDataset);
    }

    @Test
    public void should_update_dataset_reference_in_scenarios_on_rename() {
        String oldId = "old_id";
        String newId = "new_id";

        TestCaseMetadataImpl metadata = TestCaseMetadataImpl.builder().withDefaultDataset(oldId).build();
        when(testCaseRepository.findAll()).thenReturn(List.of(metadata));

        GwtTestCase testCase = GwtTestCase.builder().withMetadata(metadata).withScenario(mock(GwtScenario.class)).build();
        when(testCaseRepository.findById(any())).thenReturn(of(testCase));

        GwtTestCase expected = GwtTestCase.builder().from(testCase).withMetadata(
            TestCaseMetadataImpl.TestCaseMetadataBuilder.from(metadata).withDefaultDataset(newId).build()
        ).build();

        sut.update(of(oldId), DataSet.builder().withId(newId).withName(newId).build());

        verify(testCaseRepository, times(1)).save(expected);
    }

    @Test
    void should_remove_deleted_dataset_from_campaigns_and_scenarios() {
        String datasetId = "dataset_id";

        TestCaseMetadataImpl metadata = TestCaseMetadataImpl.builder().withDefaultDataset(datasetId).build();
        when(testCaseRepository.findAll()).thenReturn(List.of(metadata));

        GwtTestCase testCase = GwtTestCase.builder().withMetadata(metadata).withScenario(mock(GwtScenario.class)).build();
        when(testCaseRepository.findById(any())).thenReturn(of(testCase));

        Campaign campaign = CampaignBuilder.builder()
            .setId(1L)
            .setTitle("Campaign")
            .setDescription("")
            .setEnvironment("Env")
            .setTags(List.of())
            .setDatasetId(datasetId)
            .build();
        when(campaignRepository.findAll()).thenReturn(List.of(campaign));

        GwtTestCase expectedScenario = GwtTestCase.builder().from(testCase).withMetadata(
            TestCaseMetadataImpl.TestCaseMetadataBuilder.from(metadata).build()
        ).build();
        Campaign expectedCampaign = CampaignBuilder.builder().from(campaign).setDatasetId("").build();

        sut.remove(datasetId);

        verify(testCaseRepository, times(1)).save(expectedScenario);
        verify(campaignRepository, times(1)).createOrUpdate(expectedCampaign);
    }

    @Test
    public void should_return_dataset_with_id_after_save() {
        // Given
        DataSet dataset = DataSet.builder().withName("A").build();

        when(datasetRepository.save(any()))
            .thenReturn("newId");

        // When
        DataSet persistedDataset = sut.save(dataset);

        // Then
        assertThat(persistedDataset.id).isEqualTo("newId");
    }
}

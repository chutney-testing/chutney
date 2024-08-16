/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.campaign.infra;

import com.chutneytesting.campaign.infra.jpa.CampaignEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface CampaignJpaRepository extends CrudRepository<CampaignEntity, Long>, JpaSpecificationExecutor<CampaignEntity> {

    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO CAMPAIGN (ID, TITLE, DESCRIPTION) VALUES (:id, :title, :description)")
    void saveWithExplicitId(
        Long id,
        String title,
        String description);

    List<CampaignEntity> findByEnvironment(String environment);
}

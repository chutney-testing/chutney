/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package util.infra;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories(
    basePackages = {
        "com.chutneytesting.scenario.infra",
        "com.chutneytesting.campaign.infra",
        "com.chutneytesting.execution.infra.storage"
    },
    includeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.*JpaRepository$")}
)
@ComponentScan(
    basePackages = {
        "com.chutneytesting.campaign.infra",
        "com.chutneytesting.scenario.infra",
        "com.chutneytesting.execution.infra.storage"
    }
)
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableJpa {
}

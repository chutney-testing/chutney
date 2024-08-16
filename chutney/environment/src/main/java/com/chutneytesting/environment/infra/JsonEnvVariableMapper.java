/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.environment.infra;

import com.chutneytesting.environment.domain.EnvironmentVariable;
import java.util.Set;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface JsonEnvVariableMapper {
    JsonEnvVariableMapper INSTANCE = Mappers.getMapper( JsonEnvVariableMapper.class );


    EnvironmentVariable toDomain(JsonEnvVariable entity, String env);

    Set<JsonEnvVariable> fromDomains(Set<EnvironmentVariable> domain);
}

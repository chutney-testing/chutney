/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.environment.api.variable.dto;

import com.chutneytesting.environment.domain.EnvironmentVariable;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EnvironmentVariableDtoMapper {

    EnvironmentVariableDtoMapper INSTANCE = Mappers.getMapper( EnvironmentVariableDtoMapper.class );

    EnvironmentVariable toDomain(EnvironmentVariableDto dto);
    List<EnvironmentVariable> toDomains( List<EnvironmentVariableDto> dtos);
    EnvironmentVariableDto fromDomain(EnvironmentVariable domain);
}

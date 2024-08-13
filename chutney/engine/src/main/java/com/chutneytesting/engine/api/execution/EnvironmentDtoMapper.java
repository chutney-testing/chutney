/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.engine.api.execution;

import com.chutneytesting.engine.domain.execution.engine.Environment;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface EnvironmentDtoMapper {

    EnvironmentDtoMapper INSTANCE = Mappers.getMapper( EnvironmentDtoMapper.class );

    Environment toDomain(EnvironmentDto dto);
    EnvironmentDto fromDomain(Environment domain);

}

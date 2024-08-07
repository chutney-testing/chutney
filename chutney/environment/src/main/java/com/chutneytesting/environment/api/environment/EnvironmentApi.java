/*
 * Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chutneytesting.environment.api.environment;

import com.chutneytesting.environment.api.environment.dto.EnvironmentDto;
import com.chutneytesting.environment.domain.exception.AlreadyExistingEnvironmentException;
import com.chutneytesting.environment.domain.exception.CannotDeleteEnvironmentException;
import com.chutneytesting.environment.domain.exception.EnvironmentNotFoundException;
import com.chutneytesting.environment.domain.exception.InvalidEnvironmentNameException;
import java.util.Set;

public interface EnvironmentApi {
    Set<EnvironmentDto> listEnvironments();

    Set<String> listEnvironmentsNames();

    String defaultEnvironmentName();

    EnvironmentDto getEnvironment(String environmentName) throws EnvironmentNotFoundException;

    default EnvironmentDto createEnvironment(EnvironmentDto environmentMetadataDto) throws InvalidEnvironmentNameException, AlreadyExistingEnvironmentException {
        return createEnvironment(environmentMetadataDto, false);
    }

    EnvironmentDto createEnvironment(EnvironmentDto environmentMetadataDto, boolean force) throws InvalidEnvironmentNameException, AlreadyExistingEnvironmentException;

    EnvironmentDto importEnvironment(EnvironmentDto environmentDto);

    void updateEnvironment(String environmentName, EnvironmentDto environmentMetadataDto) throws InvalidEnvironmentNameException, EnvironmentNotFoundException;

    void deleteEnvironment(String environmentName) throws EnvironmentNotFoundException, CannotDeleteEnvironmentException;


}

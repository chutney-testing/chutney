/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.action.api;

import com.chutneytesting.action.api.ActionDto.InputsDto;
import com.chutneytesting.action.domain.ActionTemplate;
import com.chutneytesting.action.domain.parameter.Parameter;
import com.chutneytesting.action.spi.injectable.Input;
import com.chutneytesting.action.spi.injectable.Target;
import java.util.List;

public class ActionTemplateMapper {

    private ActionTemplateMapper() {
    }

    public static ActionDto toDto(ActionTemplate actionTemplate) {
        return new ActionDto(actionTemplate.identifier(),
            hasTarget(actionTemplate),
            toInputsDto(actionTemplate)
        );
    }

    private static boolean hasTarget(ActionTemplate actionTemplate) {
        return actionTemplate.parameters().stream().anyMatch(p -> p.rawType().equals(Target.class));
    }

    private static List<InputsDto> toInputsDto(ActionTemplate actionTemplate) {
       return actionTemplate.parameters().stream()
            .filter(parameter -> parameter.annotations().optional(Input.class).isPresent())
            .map(ActionTemplateMapper::simpleParameterToInputsDto)
            .toList();
    }

    private static InputsDto simpleParameterToInputsDto(Parameter parameter) {
        return new InputsDto(parameter.annotations().get(Input.class).value(), parameter.rawType());
    }
}

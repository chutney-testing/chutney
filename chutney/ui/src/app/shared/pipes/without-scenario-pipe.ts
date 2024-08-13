/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Pipe, PipeTransform } from '@angular/core';
import { ScenarioIndex } from '@model';

@Pipe({
    name: 'withoutScenario'
})
export class WithoutScenarioPipe implements PipeTransform {

    transform(input: Array<ScenarioIndex>, scenarioToExclude: Array<ScenarioIndex>) {
        return input.filter((item) => {
            return !scenarioToExclude === undefined || !scenarioToExclude.includes(item);
        });
    }

}

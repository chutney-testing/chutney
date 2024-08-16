/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'distinct'
})
export class DistinctPipe implements PipeTransform {

    transform(values: any [], property: string = null): any [] {
        return values.filter((value, index, self) =>
            index === self.findIndex(other =>other[property] === value[property]));
    }

}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({name: 'objectAsEntryList'})
export class ObjectAsEntryListPipe implements PipeTransform {
  transform(value, args:string[]): any {
    const keys = [];
    for(const key of Object.getOwnPropertyNames(value)) {
      keys.push({'key': key, value: value[key]});
    }
    return keys;
  }
}

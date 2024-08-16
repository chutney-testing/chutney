/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'truncate'
})
export class TruncatePipe implements PipeTransform {

  transform(value: any, args?: any, withTrail = true): any {
    const limit = args ? parseInt(args, 10) : 10;
    let trail = '...';

    if(!withTrail) {
      trail = '';
    }
    return value.length > limit ? value.substring(0, limit) + trail : value;
  }

}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { areEquals, Equals } from '@shared/equals';
import { Clonable, cloneAsPossible } from '@shared/clonable';

export class Strategy implements Equals<Strategy>, Clonable<Strategy> {

  constructor(
    public type: string,
    public parameters: Object
  ) {
  }

  public equals(obj: Strategy): boolean {
    return obj && areEquals(this.type, obj.type) && areEquals(this.parameters, obj.parameters);
  }

  public clone(): Strategy {
    return new Strategy(
      cloneAsPossible(this.type),
      cloneAsPossible(this.parameters)
    );
  }
}

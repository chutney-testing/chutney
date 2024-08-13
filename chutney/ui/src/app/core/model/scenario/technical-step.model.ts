/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { areEquals, Equals } from '@shared/equals';
import { Clonable, cloneAsPossible } from '@shared/clonable';

export class TechnicalStep implements Equals<TechnicalStep>, Clonable<TechnicalStep> {

  constructor(public task: string = '') { }

  public equals(obj: TechnicalStep): boolean {
    return obj && areEquals(this.task, obj.task);
  }

  public clone(): TechnicalStep {
    return new TechnicalStep(cloneAsPossible(this.task));
  }
}

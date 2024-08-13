/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export class Task {

  constructor(
    public identifier: string,
    public inputs: Array<InputTask> = [],
    public target: boolean
  ) { }

}

export class InputTask {

    constructor(
      public name: string,
      public type: string,
    ) { }
}

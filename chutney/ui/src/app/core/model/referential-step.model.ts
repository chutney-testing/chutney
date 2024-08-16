/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export class ReferentialStep {

  constructor(public id: string,
    public name: string,
    public usage: string,
    public steps?: Array<ReferentialStep>,
    public task?: string
  ) { }

}

export function stepsFromObjects(fromJsonObjects: Array<Object>) {
  return fromJsonObjects.map(value => stepFromObject(value));
}

export function stepFromObject(fromJsonObject: Object) {
  return new ReferentialStep(fromJsonObject['id'],
    fromJsonObject['name'],
    fromJsonObject['usage'],
    fromJsonObject['steps'],
    fromJsonObject['task']
  );
}

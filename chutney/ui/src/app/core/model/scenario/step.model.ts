/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export class Step {

    constructor(
        public name?: string,
        public target?: string,
        public type?: string,
        public inputs?: Map<string, Object>,
        public steps?: Step[]
    ) {}
}

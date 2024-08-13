/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export class Metric {
    constructor(
        public name: string,
        public tags: string,
        public value: string
    ) { }
}

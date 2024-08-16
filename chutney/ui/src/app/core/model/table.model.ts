/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export class Table {
    constructor(public columnNames: Array<string>,
        public rows: Array<Row>) { }
}

export class Row {
    constructor(public values: Array<string>) { }
}

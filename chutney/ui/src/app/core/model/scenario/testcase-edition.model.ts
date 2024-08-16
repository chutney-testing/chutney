/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export class TestCaseEdition {
    constructor(
        public testCaseId: string,
        public testCaseVersion: number,
        public editionStartDate: Date,
        public editionUser: string) {
    }
}

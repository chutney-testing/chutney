/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export class AgentInfo {
    constructor(
        public name: string,
        public host: string,
        public port: number,
    ) { }
}

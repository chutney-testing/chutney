/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export class JiraPluginConfiguration {
    constructor(
        public url: string,
        public username: string,
        public password: string,
        public urlProxy: string,
        public userProxy: string,
        public passwordProxy: string) {
    }
}

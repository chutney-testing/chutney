/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Entry } from './entry.model';

export class Environment {
    constructor(
        public name: string,
        public description: string,
        public targets: Target [] = [],
        public variables: EnvironmentVariable[] = []) {
    }
}

export class Target {
    constructor(
        public name: string,
        public url: string,
        public properties: Entry [] = [],
        public environment: string = null,
    ) {
    }
}

export class EnvironmentVariable {
    constructor(
        public key: string,
        public value: string,
        public env: string = null
    ) {
    }
}

export class TargetFilter {
    constructor(
        public name: string = null,
        public environment: string = null,
    ) {
    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export class TargetId {
    constructor(
        public name: string
    ) {
    }

    public asId() {
        return this.name;
    }

    public htmlDisplay() {
        return '<b>' + this.name + '</b>';
    }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { areEquals, Equals } from '@shared/equals';
import { Clonable, cloneAsPossible } from '@shared/clonable';
import { Execution } from '@core/model/scenario/execution.model';

export class TestCase implements Equals<TestCase>, Clonable<TestCase> {

    constructor(
        public id?: string,
        public title?: string,
        public description?: string,
        public content?: string,
        public repositorySource?: string,
        public creationDate?: Date,
        public updateDate?: Date,
        public version?: number,
        public author?: string,
        public tags: Array<string> = [],
        public executions?: Array<Execution>,
        public defaultDataset?: string
    ) {
    }

    public equals(obj: TestCase): boolean {
        return obj
            && areEquals(this.title, obj.title)
            && areEquals(this.description, obj.description)
            && areEquals(this.content, obj.content)
            && areEquals(this.tags, obj.tags);
    }

    public clone(): TestCase {
        return new TestCase(
            null,
            cloneAsPossible(this.title),
            cloneAsPossible(this.description),
            cloneAsPossible(this.content),
            null,
            null,
            null,
            null,
            null,
            cloneAsPossible(this.tags),
            null,
            null
        );
    }

    static fromRaw(raw: any): TestCase {
        return new TestCase(
            raw.id,
            raw.title,
            raw.description,
            raw.content,
            'local',
            raw.creationDate,
            raw.updateDate,
            raw.version,
            raw.author,
            raw.tags,
            raw.executions,
            raw.defaultDataset
        );
    }
}

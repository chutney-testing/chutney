/**
 * Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { areEquals, Clonable, cloneAsPossible } from '@shared';

export class Dataset {
    constructor(
        public name: string = '',
        public description: string = '',
        public tags: Array<string> = [],
        public lastUpdated: Date,
        public uniqueValues: Array<KeyValue>,
        public multipleValues: Array<Array<KeyValue>>,
        public id?: string) {
    }

    getMultipleValueHeader(): Array<string> {
        if (this.multipleValues.length > 0) {
            return this.multipleValues[0].map(v => v.key);
        }
        return [];
    }

    public equals(obj: Dataset): boolean {
        return obj
            && areEquals(this.name, obj.name)
            && areEquals(this.description, obj.description)
            && areEquals(this.tags, obj.tags)
            && areEquals(this.uniqueValues, obj.uniqueValues)
            && areEquals(this.multipleValues, obj.multipleValues);
    }
}

export class KeyValue implements Clonable<KeyValue> {

    constructor(
        public key: string,
        public value: any
    ) {
    }

    public clone(): KeyValue {
        return new KeyValue(
            cloneAsPossible(this.key),
            cloneAsPossible(this.value)
        );
    }

    public equals(obj: KeyValue): boolean {
        return obj
            && areEquals(this.key, obj.key)
            && areEquals(this.value, obj.value);
    }
}

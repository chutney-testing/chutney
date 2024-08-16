/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Injectable } from '@angular/core';

import * as hjson from 'hjson';

@Injectable()
export class HjsonParserService {

    parse(content: string): string {
        return JSON.stringify(hjson.parse(content));
    }
}

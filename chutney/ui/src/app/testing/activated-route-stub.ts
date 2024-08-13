/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { of } from 'rxjs';
import { convertToParamMap } from '@angular/router';

export class ActivatedRouteStub {

    params = of({});
    queryParams = of({});

    snapshot = {
        queryParamMap: {},
        _lastPathIndex: 0
    };

    constructor() {
    }

    setParamMap(params?: Object) {
        this.params = of(params);
        this.queryParams = of(params);
    }

    setSnapshotQueryParamMap(params?: Object) {
        this.snapshot.queryParamMap = convertToParamMap(params);
    }
}

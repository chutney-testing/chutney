/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { environment } from '@env/environment';
import { Execution } from '@core/model';

@Injectable({
  providedIn: 'root'
})
export class DatabaseAdminService {

  private adminUrl = '/api/v1/admin/database';

  constructor(private http: HttpClient) { }

  getExecutionReportMatchQuery(query: string): Observable<Execution[]> {
    return this.http.get<Execution[]>(environment.backend + this.adminUrl + '/execution', {params: {query: query}})
    .pipe(
      map((res: Execution[]) => {
          return res.map((execution) => Execution.deserialize(execution));
      })
    )
  }

  compactDatabase(): Observable<number[]> {
    return this.http.post<number[]>(environment.backend + this.adminUrl + '/compact', null);
  }

  computeDatabaseSize(): Observable<number> {
    return this.http.get<number>(environment.backend + this.adminUrl + '/size');
  }
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '@env/environment';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StepService {

  private stepUrl = '/api/steps/v1';

  constructor(private http: HttpClient) {
  }

  findById(stepId: string): Observable<Object> {
    return this.http.get(environment.backend + this.stepUrl + '/' + encodeURIComponent(stepId));
  }
}

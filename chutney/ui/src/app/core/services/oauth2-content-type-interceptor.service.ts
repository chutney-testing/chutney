/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Injectable } from '@angular/core';
import { SsoService } from '@core/services/sso.service';

@Injectable()
export class OAuth2ContentTypeInterceptor implements HttpInterceptor {

    constructor(private ssoService: SsoService) {}

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        const isTokenEndpoint = this.ssoService.headers && req.url.startsWith(this.ssoService.tokenEndpoint);
        if (isTokenEndpoint) {
            const modifiedReq = req.clone({
                setHeaders: this.ssoService.headers
            });
            return next.handle(modifiedReq);
        }
        return next.handle(req);
    }
}

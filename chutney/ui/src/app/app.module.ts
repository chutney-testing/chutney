/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

// Core
import { BrowserModule } from '@angular/platform-browser';
import { APP_INITIALIZER, NgModule } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
// External libs
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { MissingTranslationHandler, TranslateLoader, TranslateModule } from '@ngx-translate/core';

import { ToastrModule } from 'ngx-toastr';
import { DragulaModule } from 'ng2-dragula';
// Internal common
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app-routing.module';
import { SharedModule } from '@shared/shared.module';
import { CoreModule } from '@core/core.module';
import { BsModalService, ModalModule } from 'ngx-bootstrap/modal';
import { ThemeService } from '@core/theme/theme.service';
import { DefaultMissingTranslationHandler, HttpLoaderFactory } from '@core/initializer/app.translate.factory';
import { themeInitializer } from '@core/initializer/theme.initializer';
import { OAuthModule } from 'angular-oauth2-oidc';
import { SsoService } from "@core/services/sso.service";

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    // Core
    BrowserModule,
    BrowserAnimationsModule,
    CommonModule,
    AppRoutingModule,
    CoreModule,
    // External libs
    FormsModule,
    HttpClientModule,
    DragulaModule.forRoot(),
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: HttpLoaderFactory,
        deps: [HttpClient]
      },
      missingTranslationHandler: { provide: MissingTranslationHandler, useClass: DefaultMissingTranslationHandler }
    }),
    ToastrModule.forRoot({
      timeOut: 10000,
      positionClass: 'toast-top-full-width',
      preventDuplicates: true,
    }),
    ModalModule.forRoot(),
    NgbModule,
    // Internal common
    SharedModule,
    OAuthModule.forRoot()
  ],
  providers: [BsModalService,
      {
          provide: APP_INITIALIZER,
          useFactory: themeInitializer,
          deps: [ThemeService],
          multi: true
      }
  ],
  bootstrap: [AppComponent]
})
export class ChutneyAppModule {
    constructor(private ssoOpenIdConnectService: SsoService) {
        this.ssoOpenIdConnectService.fetchSsoConfig()
    }
}






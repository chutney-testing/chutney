/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { HostListener, Injectable } from '@angular/core';

@Injectable()
export abstract class CanDeactivatePage {
  abstract canDeactivatePage(): boolean;

  @HostListener('window:beforeunload', ['$event'])
  unloadNotification($event: any) {
    if (!this.canDeactivatePage()) {
      $event.returnValue = true;
    }
  }
}

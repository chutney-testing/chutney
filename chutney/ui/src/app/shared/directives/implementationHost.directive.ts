/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Directive, ViewContainerRef } from '@angular/core';

@Directive({
  selector: '[implementation-host]',
})
export class ImplementationHostDirective {
  constructor(public viewContainerRef: ViewContainerRef) { }
}

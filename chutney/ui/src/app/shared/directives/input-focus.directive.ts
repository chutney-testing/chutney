/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Directive, ElementRef, OnInit, Renderer2 } from '@angular/core';

@Directive({
    selector : '[chutneyFocusOnShow]'
  })
export class InputFocusDirective implements OnInit {
    constructor(public renderer: Renderer2, public elementRef: ElementRef) {}

    ngOnInit() {
      this.renderer.selectRootElement(this.elementRef.nativeElement).focus();
    }
}

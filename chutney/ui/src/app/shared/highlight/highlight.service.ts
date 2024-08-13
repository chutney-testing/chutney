/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class HighLightJService {

  constructor() {
  }

  highlightElement(baseElement: Element, codeSelectorAll: string = 'pre code') {
      const codes = baseElement.querySelectorAll(codeSelectorAll);
      //TODO https://www.npmjs.com/package/ngx-highlightjs codes.forEach(code => hljs.highlightBlock(code));
  }
}

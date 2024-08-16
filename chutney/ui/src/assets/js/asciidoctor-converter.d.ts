/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export default class AsciidocConverter {
  convert(content: string): string;

  styleEmbeddedDocWithLeftToc(baseAsciiDocElement: Element, styleClass: string);

  isElementFromToc(baseAsciiDocElement: Element, element: Element);
}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export class ChutneyState {
  constructor(
    public tags: Array<String> = [],
    public campaignTags: Array<String> = [],
    public noTag: boolean,
    public campaignNoTag: boolean
  ) { }
}

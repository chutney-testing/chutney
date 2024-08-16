/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export class GitRemoteConfig {
    constructor(
      public name: string,
      public url: string,
      public branch: string,
      public privateKeyPath: string,
      public privateKeyPassphrase: string,
    ) { }
  }

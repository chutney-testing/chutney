/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

export class GitRepository {
    constructor(
      public id: number,
      public url: string,
      public sourceDirectory: string,
      public name: string,
    ) { }

    static deserializeGitRepositories(jsonObject: any): GitRepository[] {
      return jsonObject.map(execution => GitRepository.deserialize(execution));
    }

    static deserialize(jsonObject: any): GitRepository {
      return new GitRepository(
        jsonObject.id,
        jsonObject.url,
        jsonObject.sourceDirectory,
        jsonObject.name
      );
    }
  }

/**
 * Copyright 2017-2024 Enedis
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

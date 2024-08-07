/*
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

package com.chutneytesting.admin.domain;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface BackupRepository {

    String save(Backup backup);
    Backup read(String backupId);
    void delete(String backupId);
    List<Backup> list();
    void getBackupData(String backupId, OutputStream outputStream) throws IOException;

    List<String> getBackupables();
}

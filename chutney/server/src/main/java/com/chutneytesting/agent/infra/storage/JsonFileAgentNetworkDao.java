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

package com.chutneytesting.agent.infra.storage;

import static com.chutneytesting.tools.file.FileUtils.initFolder;
import static java.util.Optional.of;

import com.chutneytesting.server.core.domain.tools.ZipUtils;
import com.chutneytesting.tools.ThrowingRunnable;
import com.chutneytesting.tools.ThrowingSupplier;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.io.Files;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.ZipOutputStream;

public class JsonFileAgentNetworkDao {

    static final Path ROOT_DIRECTORY_NAME = Paths.get("agents");
    static final String AGENTS_FILE_NAME = "endpoints.json";
    private final ObjectMapper objectMapper;
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock(false);
    private final File file;

    public JsonFileAgentNetworkDao(String storeFolderPath) {
        this(storeFolderPath, buildObjectMapper());
    }

    JsonFileAgentNetworkDao(String storeFolderPath, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        Path dir = Paths.get(storeFolderPath).resolve(ROOT_DIRECTORY_NAME).toAbsolutePath();
        initFolder(dir);
        this.file = dir.resolve(AGENTS_FILE_NAME).toFile();
        file.delete(); // TODO keep/refresh network configuration on restart
    }

    public Optional<AgentNetworkForJsonFile> read() {
        return executeWithLocking(rwLock.readLock(), () -> {
            if (!file.exists()) return Optional.empty();
            return of(objectMapper.readValue(file, AgentNetworkForJsonFile.class));
        });
    }

    public void save(AgentNetworkForJsonFile agentEndpointsConfiguration) {
        executeWithLocking(rwLock.writeLock(), (ThrowingRunnable) () -> {
            Files.createParentDirs(file);
            objectMapper.writeValue(file, agentEndpointsConfiguration);
        });
    }

    public void backup(OutputStream outputStream) {
        try (ZipOutputStream zipOutPut = new ZipOutputStream(new BufferedOutputStream(outputStream, 4096))) {
            ZipUtils.compressFile(this.file, this.file.getName(), zipOutPut);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private <T> T executeWithLocking(Lock lock, ThrowingSupplier<T, ? extends Exception> supplier) {
        lock.lock();
        try {
            return supplier.unsafeGet();
        } finally {
            lock.unlock();
        }
    }

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper()
            .findAndRegisterModules()
            .enable(SerializationFeature.INDENT_OUTPUT);

        return objectMapper.setVisibility(
            objectMapper.getSerializationConfig()
                .getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
        );
    }
}

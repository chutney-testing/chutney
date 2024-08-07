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

package com.chutneytesting.action.ssh;

import static com.chutneytesting.action.spi.validation.ActionValidatorsUtils.durationValidation;
import static com.chutneytesting.action.spi.validation.ActionValidatorsUtils.notBlankStringValidation;
import static com.chutneytesting.action.spi.validation.ActionValidatorsUtils.targetValidation;
import static com.chutneytesting.action.spi.validation.Validator.getErrorsFrom;
import static com.chutneytesting.action.ssh.SshClientFactory.DEFAULT_TIMEOUT;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

import com.chutneytesting.action.spi.Action;
import com.chutneytesting.action.spi.ActionExecutionResult;
import com.chutneytesting.action.spi.injectable.Input;
import com.chutneytesting.action.spi.injectable.Logger;
import com.chutneytesting.action.spi.injectable.Target;
import com.chutneytesting.action.spi.time.Duration;
import com.chutneytesting.action.ssh.sftp.ChutneySftpClient;
import com.chutneytesting.action.ssh.sftp.SftpClientImpl;
import java.util.List;
import java.util.Map;

public class SftpFileInfoAction implements Action {

    private final Target target;
    private final Logger logger;
    private final String file;
    private final String timeout;

    public SftpFileInfoAction(Target target, Logger logger, @Input("file") String file, @Input("timeout") String timeout) {
        this.target = target;
        this.logger = logger;
        this.file = file;
        this.timeout = defaultIfEmpty(timeout, DEFAULT_TIMEOUT);
    }

    @Override
    public List<String> validateInputs() {
        return getErrorsFrom(
            notBlankStringValidation(file, "file"),
            durationValidation(timeout, "timeout"),
            targetValidation(target)
        );
    }

    @Override
    public ActionExecutionResult execute() {
        try (ChutneySftpClient client = SftpClientImpl.buildFor(target, Duration.parseToMs(timeout), logger)) {
            Map<String, Object> info = client.getAttributes(file);
            return ActionExecutionResult.ok(info);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ActionExecutionResult.ko();
        }
    }

}

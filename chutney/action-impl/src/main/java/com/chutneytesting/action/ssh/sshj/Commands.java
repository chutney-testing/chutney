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

package com.chutneytesting.action.ssh.sshj;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Commands {

    private static final Logger LOGGER = LoggerFactory.getLogger(Commands.class);

    public final List<Command> all;

    private Commands(List<Command> commands) {
        this.all = commands;
    }

    public static Commands from(List<Object> commands) {
        List<Command> cmds = ofNullable(commands).orElse(emptyList()).stream()
            .map(Commands::buildCommand)
            .collect(Collectors.toList());

        return new Commands(cmds);
    }

    @SuppressWarnings("unchecked")
    private static Command buildCommand(Object command) {
        if (command instanceof String stringCommand) {
            return new Command(stringCommand);
        }

        if (command instanceof Map) {
            return new Command(((Map<String, String>) command).get("command"),
                ((Map<String, String>) command).get("timeout"));
        }

        throw new IllegalStateException("Unable to understand command: " + command.toString());
    }

    public List<CommandResult> executeWith(SshClient sshClient) throws IOException {
        List<CommandResult> results = new ArrayList<>();

        for (Command command : this.all) {
            LOGGER.debug("COMMANDS :: {}", command);
            CommandResult result = command.executeWith(sshClient);
            results.add(result);
            LOGGER.debug("RESULT :: {}", result);
        }

        return results;
    }

}

/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

package com.chutneytesting.agent.domain.network;

import com.chutneytesting.agent.domain.configure.NetworkConfiguration;
import java.util.Optional;
import org.immutables.value.Value;

@Value.Immutable
public interface NetworkDescription {
    NetworkConfiguration configuration();

    AgentGraph agentGraph();

    // TODO why optional ?
    Optional<Agent> localAgent();
}

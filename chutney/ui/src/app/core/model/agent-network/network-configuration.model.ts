/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { AgentInfo } from '.';

export class NetworkConfiguration {
    constructor(
        public agentNetworkConfiguration: Array<AgentInfo>
    ) {

    }
}

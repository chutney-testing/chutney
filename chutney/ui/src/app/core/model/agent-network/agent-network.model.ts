/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { AgentGraphe, NetworkConfiguration } from '.';

export class AgentNetwork {
    constructor(
        readonly graphe: AgentGraphe,
        readonly networkConfiguration: NetworkConfiguration
    ) { }
}

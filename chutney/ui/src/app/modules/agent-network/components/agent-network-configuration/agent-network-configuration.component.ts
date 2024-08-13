/*
 * SPDX-FileCopyrightText: 2017-2024 Enedis
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 */

import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AgentInfo, NetworkConfiguration } from '@model';
import { AgentNetworkService } from '@core/services';


@Component({
    selector: 'chutney-agent-network-configuration',
    templateUrl: './agent-network-configuration.component.html',
    styleUrls: ['./agent-network-configuration.component.scss']
})
export class AgentNetworkConfigurationComponent {
    @Input() currentConfiguration: NetworkConfiguration;
    @Output() configurationUpdate = new EventEmitter();

    constructor(private agentService: AgentNetworkService) { }

    removeAgent(configurationAgent) {
        const index = this.currentConfiguration.agentNetworkConfiguration.indexOf(configurationAgent);
        this.currentConfiguration.agentNetworkConfiguration.splice(index, 1);
    }

    save() {
        this.configurationUpdate.emit('Propagation en cours');
        this.agentService.sendAndSaveConfiguration(this.currentConfiguration)
            .subscribe(
                (res) => this.configurationUpdate.emit('Propagation terminée'),
                (error) => this.configurationUpdate.emit('Erreur: ' + error));
    }

    addServer() {
        this.currentConfiguration.agentNetworkConfiguration.push(new AgentInfo('', '', 8350));
    }
}

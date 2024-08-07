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

import { AgentNetworkComponent } from './components/agent-network/agent-network.component';
import { Routes } from '@angular/router';
import { Authorization } from '@model';

export const AgentNetworkRoute: Routes = [
    {
        path: '',
        component: AgentNetworkComponent,
        data: { 'authorizations': [ Authorization.ADMIN_ACCESS ] }
    }
];

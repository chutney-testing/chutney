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

package com.chutneytesting.agent.api.dto;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * DTO for ExploreResult transport.
 */
public class ExploreResultApiDto {

    public Set<AgentLinkEntity> agentLinks = new LinkedHashSet<>();

    public final Set<TargetLinkEntity> targetLinks = new LinkedHashSet<>();

    public static class AgentLinkEntity {
        public String source;
        public String destination;

        public AgentLinkEntity() {
        }

        public AgentLinkEntity(String source, String destination) {
            this.source = source;
            this.destination = destination;
        }
    }

    public static class TargetLinkEntity {
        public final String source;
        public final TargetIdEntity destination;

        public TargetLinkEntity(String source, TargetIdEntity destination) {
            this.source = source;
            this.destination = destination;
        }
    }
}

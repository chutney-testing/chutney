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

package com.chutneytesting.action.kafka;

import static java.util.Optional.of;
import static org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG;

import com.chutneytesting.action.spi.injectable.Target;
import java.net.URI;

final class KafkaClientFactoryHelper {

    static String resolveBootStrapServerConfig(Target target) {
        return target.property(BOOTSTRAP_SERVERS_CONFIG)
            .or(() -> of(target.uri()).map(URI::getAuthority))
            .orElseGet(() -> target.uri().toString());
    }
}

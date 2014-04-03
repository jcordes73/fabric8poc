/*
 * #%L
 * Gravia :: Integration Tests :: Common
 * %%
 * Copyright (C) 2010 - 2014 JBoss by Red Hat
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.fabric8.spi.internal;

import io.fabric8.api.ConfigurationItem;
import io.fabric8.api.ConfigurationItemBuilder;

import java.util.Map;

public final class DefaultConfigurationItemBuilder implements ConfigurationItemBuilder {

    private String identity;
    private Map<String, String> config;

    @Override
    public ConfigurationItemBuilder addIdentity(String identity) {
        this.identity = identity;
        return this;
    }

    @Override
    public ConfigurationItemBuilder setConfiguration(Map<String, String> config) {
        this.config = config;
        return this;
    }

    @Override
    public ConfigurationItem getConfigurationItem() {
        return new DefaultConfigurationItem(identity, config);
    }
}
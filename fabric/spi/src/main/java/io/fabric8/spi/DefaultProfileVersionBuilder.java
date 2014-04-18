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
package io.fabric8.spi;

import io.fabric8.api.AttributeKey;
import io.fabric8.api.ContainerIdentity;
import io.fabric8.api.ProfileIdentity;
import io.fabric8.api.ProfileVersion;
import io.fabric8.api.ProfileVersionBuilder;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.jboss.gravia.resource.Version;

public final class DefaultProfileVersionBuilder extends AbstractAttributableBuilder<ProfileVersionBuilder> implements ProfileVersionBuilder {

    private Version identity;

    @Override
    public ProfileVersionBuilder addIdentity(Version version) {
        this.identity = version;
        return this;
    }

    @Override
    public ProfileVersion getProfileVersion() {
        return new ProfileVersionImpl(getAttributes());
    }

    class ProfileVersionImpl extends AttributeSupport implements ProfileVersion {

        private ProfileVersionImpl(Map<AttributeKey<?>, Object> attributes) {
            super(attributes);
        }

        @Override
        public Version getIdentity() {
            return identity;
        }

        @Override
        public Set<ContainerIdentity> getContainers() {
            return Collections.emptySet();
        }

        @Override
        public Set<ProfileIdentity> getProfiles() {
            return Collections.emptySet();
        }
    }
}

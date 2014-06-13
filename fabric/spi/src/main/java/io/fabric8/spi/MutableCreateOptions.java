/*
 * #%L
 * Fabric8 :: API
 * %%
 * Copyright (C) 2014 Red Hat
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
import io.fabric8.api.CreateOptions;
import io.fabric8.api.VersionIdentity;

import java.util.List;
import java.util.Map;

/**
 * Mutable container create options
 *
 * @author thomas.diesler@jboss.com
 * @since 05-Jun-2014
 */
public interface MutableCreateOptions extends CreateOptions {

    void setIdentity(ContainerIdentity identity);

    void setVersion(VersionIdentity version);

    void setProfiles(List<String> profiles);

    <T> void addAttribute(AttributeKey<T> key, T value);

    void addAttributes(Map<AttributeKey<?>, Object> atts);

    void validate();
}
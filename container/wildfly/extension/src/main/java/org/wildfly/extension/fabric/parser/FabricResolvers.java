/*
 * #%L
 * Fabric8 :: Container :: WildFly :: Extension
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

package org.wildfly.extension.fabric.parser;

import org.jboss.as.controller.descriptions.ResourceDescriptionResolver;
import org.jboss.as.controller.descriptions.StandardResourceDescriptionResolver;

/**
 * @since 13-Nov-2013
 */
final class FabricResolvers {

    static final String RESOURCE_NAME = FabricResolvers.class.getPackage().getName() + ".LocalDescriptions";

    static ResourceDescriptionResolver getResolver(String keyPrefix) {
        return new StandardResourceDescriptionResolver(keyPrefix, RESOURCE_NAME, SecurityActions.getClassLoader(FabricResolvers.class), true, true);
    }

}

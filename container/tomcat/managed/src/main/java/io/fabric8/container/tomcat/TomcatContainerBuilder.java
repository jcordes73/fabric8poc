/**
 * Copyright (C) FuseSource, Inc.
 * http://fusesource.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.fabric8.container.tomcat;

import io.fabric8.spi.AbstractManagedContainerBuilder;


/**
 * The Tomcat managed container builder
 *
 * @author Thomas.Diesler@jboss.com
 * @since 26-Feb-2014
 */
public class TomcatContainerBuilder extends AbstractManagedContainerBuilder<TomcatContainerBuilder, TomcatCreateOptions> {

    public TomcatContainerBuilder() {
        super(new TomcatCreateOptions());
    }

    @Override
    public TomcatManagedContainer getManagedContainer() {
        return new TomcatManagedContainer();
    }

    public TomcatContainerBuilder setJmxPort(int jmxPort) {
        options.setJmxPort(jmxPort);
        return this;
    }
}
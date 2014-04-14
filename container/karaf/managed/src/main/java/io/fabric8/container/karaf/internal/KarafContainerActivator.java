/*
 * #%L
 * Gravia :: Repository
 * %%
 * Copyright (C) 2012 - 2014 JBoss by Red Hat
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
package io.fabric8.container.karaf.internal;

import io.fabric8.api.container.ManagedContainerBuilder;
import io.fabric8.container.karaf.KarafManagedContainerBuilder;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * Activate the {@link KarafManagedContainerBuilder} in the runtime.
 *
 * @author thomas.diesler@jboss.com
 * @since 26-Feb-2014
 */
public final class KarafContainerActivator implements BundleActivator {

    @SuppressWarnings("rawtypes")
    private ServiceRegistration<ManagedContainerBuilder> registration;

    @Override
    public void start(BundleContext context) throws Exception {
        ManagedContainerBuilder<?, ?> service = new KarafManagedContainerBuilder();
        registration = context.registerService(ManagedContainerBuilder.class, service, null);
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        if (registration != null) {
            registration.unregister();
        }
    }
}

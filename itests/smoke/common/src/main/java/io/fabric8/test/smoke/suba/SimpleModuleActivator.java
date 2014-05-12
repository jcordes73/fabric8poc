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
package io.fabric8.test.smoke.suba;

import io.fabric8.api.ServiceLocator;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.StandardMBean;

import org.jboss.gravia.resource.ResourceIdentity;
import org.jboss.gravia.runtime.Module;
import org.jboss.gravia.runtime.Module.State;
import org.jboss.gravia.runtime.ModuleActivator;
import org.jboss.gravia.runtime.ModuleContext;

public class SimpleModuleActivator implements ModuleActivator {

    public interface ModuleState {
        ResourceIdentity getResourceIdentity();
        State getModuleState();
    }

    @Override
    public void start(final ModuleContext context) throws Exception {
        MBeanServer server = ServiceLocator.getRequiredService(context, MBeanServer.class);
        ModuleState moduleState = new ModuleState() {

            @Override
            public ResourceIdentity getResourceIdentity() {
                return context.getModule().getIdentity();
            }

            @Override
            public State getModuleState() {
                return context.getModule().getState();
            }
        };
        StandardMBean mbean = new StandardMBean(moduleState, ModuleState.class);
        server.registerMBean(mbean, getObjectName(context.getModule()));
    }

    @Override
    public void stop(ModuleContext context) throws Exception {
        MBeanServer server = ServiceLocator.getRequiredService(context, MBeanServer.class);
        server.unregisterMBean(getObjectName(context.getModule()));
    }

    private static ObjectName getObjectName(Module module) throws MalformedObjectNameException {
        ResourceIdentity identity = module.getIdentity();
        return new ObjectName("test:name=" + identity.getSymbolicName() + ",version=" + identity.getVersion());
    }
}

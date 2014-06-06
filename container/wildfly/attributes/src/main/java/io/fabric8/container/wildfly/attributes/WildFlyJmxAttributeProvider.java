/*
 * Copyright (C) 2010 - 2014 JBoss by Red Hat
 *
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
 */

package io.fabric8.container.wildfly.attributes;


import io.fabric8.api.ContainerAttributes;
import io.fabric8.spi.AttributeProvider;
import io.fabric8.spi.JmxAttributeProvider;
import io.fabric8.spi.RuntimeService;
import io.fabric8.spi.scr.AbstractAttributeProvider;
import io.fabric8.spi.scr.ValidatingReference;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;

@Component(policy = ConfigurationPolicy.IGNORE, immediate = true)
@Service({ AttributeProvider.class, JmxAttributeProvider.class})
@Properties(
        @Property(name = "type", value = ContainerAttributes.TYPE)
)
public class WildFlyJmxAttributeProvider extends AbstractAttributeProvider implements JmxAttributeProvider {

    private static final String JMX_REMOTE_PORT = "jboss.management.http.port";
    private static final int DEFAULT_JMX_REMOTE_PORT = 9990;

    private static final String JMX_URL_FORMAT = "service:jmx:http-remoting-jmx://${container:%s/fabric8.ip}:%d";

    @Reference(referenceInterface = RuntimeService.class)
    private final ValidatingReference<RuntimeService> runtimeService = new ValidatingReference<>();

    private int jmxRemotePort;
    private String jmxServerUrl;
    private String runtimeId;

    @Activate
    void activate() throws Exception {
        runtimeId = runtimeService.get().getProperty(RuntimeService.RUNTIME_IDENTITY);
        jmxRemotePort = Integer.parseInt(runtimeService.get().getProperty(JMX_REMOTE_PORT, "" + DEFAULT_JMX_REMOTE_PORT));
        updateAttributes();
        activateComponent();
    }

    @Deactivate
    void deactivate() {
        deactivateComponent();
    }

    @Override
    public String getJmxServerUrl() {
        return jmxServerUrl;
    }

    private void updateAttributes() {
        putAttribute(ContainerAttributes.ATTRIBUTE_KEY_JMX_SERVER_URL, getJmxUrl(runtimeId, jmxRemotePort));
    }

    private String getJmxUrl(String name, int port)  {
        return jmxServerUrl = String.format(JMX_URL_FORMAT, name, port);
    }

    void bindRuntimeService(RuntimeService service) {
        runtimeService.bind(service);
    }
    void unbindRuntimeService(RuntimeService service) {
        runtimeService.unbind(service);
    }
}

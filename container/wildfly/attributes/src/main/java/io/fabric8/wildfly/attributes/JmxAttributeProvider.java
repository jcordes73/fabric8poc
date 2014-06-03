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

package io.fabric8.wildfly.attributes;


import io.fabric8.spi.AttributeProvider;
import io.fabric8.api.ContainerAttributes;
import io.fabric8.spi.Configurer;
import io.fabric8.spi.RuntimeService;
import io.fabric8.spi.scr.AttributeProviderComponent;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@Component(immediate = true)
@Service(AttributeProvider.class)
@Properties(
        @Property(name = "type", value = ContainerAttributes.TYPE)
)
public class JmxAttributeProvider extends AttributeProviderComponent  {

    private static final Logger LOGGER = LoggerFactory.getLogger(JmxAttributeProvider.class);
    private static final String JMX_REMOTE_PORT = "jboss.management.http.port";
    private static final int DEFAULT_JMX_REMOTE_PORT = 9990;

    private static final String JMX_URL_FORMAT = "service:jmx:http-remoting-jmx://${container:%s/fabric8.ip}:%d";

    @Property(name = JMX_REMOTE_PORT, value = "${" + JMX_REMOTE_PORT + "}")
    int jmxRemotePort = DEFAULT_JMX_REMOTE_PORT;
    @Property(name ="runtimeId", value = "${"+RuntimeService.RUNTIME_IDENTITY+"}")
    private String runtimeId;
    @Reference
    private Configurer configurer;

    @Activate
    void activate(Map<String, Object> configuration) throws Exception {
        configurer.configure(configuration, this);
        jmxRemotePort = jmxRemotePort > 0 ? jmxRemotePort : DEFAULT_JMX_REMOTE_PORT;
        updateAttributes();
        activateComponent();
    }

    @Deactivate
    void deactivate() {
        deactivateComponent();
    }

    private void updateAttributes() {
        putAttribute(ContainerAttributes.ATTRIBUTE_KEY_JMX_SERVER_URL, getJmxUrl(runtimeId, jmxRemotePort));
    }

    private String getJmxUrl(String name, int port)  {
        return String.format(JMX_URL_FORMAT, name, port);
    }
}


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

package io.fabric8.tomcat.attributes;

import io.fabric8.api.ContainerAttributes;
import io.fabric8.spi.AttributeProvider;
import io.fabric8.spi.RuntimeService;
import io.fabric8.spi.scr.AbstractAttributeProvider;
import io.fabric8.spi.scr.ValidatingReference;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import org.apache.catalina.Server;
import org.apache.catalina.connector.Connector;
import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.ConfigurationPolicy;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(policy = ConfigurationPolicy.IGNORE, immediate = true)
@Service(AttributeProvider.class)
@Properties(@Property(name = "type", value = ContainerAttributes.TYPE))
public class HttpAttributeProvider extends AbstractAttributeProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpAttributeProvider.class);

    private static final int DEFAULT_HTTP_REMOTE_PORT = 8080;
    private static final int DEFAULT_HTTPS_REMOTE_PORT = 8443;

    private static final String HTTP_URL_FORMAT = "%s://${container:%s/fabric8.ip}:%d";

    @Reference(referenceInterface = MBeanServer.class)
    private final ValidatingReference<MBeanServer> mbeanServer = new ValidatingReference<MBeanServer>();
    @Reference(referenceInterface = RuntimeService.class)
    private final ValidatingReference<RuntimeService> runtimeService = new ValidatingReference<>();

    private final Set<Connector> httpConnectors = new LinkedHashSet<Connector>();
    private final Set<Connector> httpsConnectors = new LinkedHashSet<Connector>();
    private String runtimeId;

    @Activate
    void activate() throws Exception {
        runtimeId = runtimeService.get().getProperty(RuntimeService.RUNTIME_IDENTITY);
        activateInternal();
        updateAttributes();
        activateComponent();
    }

    @Deactivate
    void deactivate() {
        deactivateComponent();
    }

    private void activateInternal() throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        Server server = getServer();
        org.apache.catalina.Service[] services = server.findServices();
        for (org.apache.catalina.Service service : services) {
            for (Connector connector : service.findConnectors()) {
                if (connector.getScheme().equals("http")) {
                    httpConnectors.add(connector);
                } else if (connector.getScheme().equals("https")) {
                    httpsConnectors.add(connector);
                }
            }
        }
    }

    private void updateAttributes() {
        try {
            boolean httpEnabled = isHttpEnabled();
            boolean httpsEnabled = isHttpsEnabled();
            String protocol = httpsEnabled && !httpEnabled ? "https" : "http";
            int httpPort = httpsEnabled && !httpEnabled ? getHttpsPort() : getHttpPort();
            String httpUrl = getHttpUrl(protocol, runtimeId, httpPort);
            putAttribute(ContainerAttributes.ATTRIBUTE_KEY_HTTP_URL, httpUrl);
        } catch (Exception e) {
            LOGGER.warn("Failed to get http attributes.", e);
        }
    }

    private Server getServer() throws MalformedObjectNameException, AttributeNotFoundException, MBeanException, ReflectionException, InstanceNotFoundException {
        ObjectName name = new ObjectName("Catalina", "type", "Server");
        return (Server) mbeanServer.get().getAttribute(name, "managedResource");
    }

    private boolean isHttpEnabled() throws IOException {
        return !httpConnectors.isEmpty();
    }

    private boolean isHttpsEnabled() throws IOException {
        return !httpsConnectors.isEmpty();
    }

    private int getHttpPort() {
        int port = DEFAULT_HTTP_REMOTE_PORT;
        for (Connector connector : httpConnectors) {
            return connector.getPort();
        }
        return port;
    }

    private int getHttpsPort() throws InterruptedException, IOException {
        int port = DEFAULT_HTTPS_REMOTE_PORT;
        for (Connector connector : httpsConnectors) {
            return connector.getPort();
        }
        return port;
    }

    private String getHttpUrl(String protocol, String name, int port) {
        return String.format(HTTP_URL_FORMAT, name);
    }

    void bindMbeanServer(MBeanServer service) {
        this.mbeanServer.bind(service);
    }
    void unbindMbeanServer(MBeanServer service) {
        this.mbeanServer.unbind(service);
    }

    void bindRuntimeService(RuntimeService service) {
        runtimeService.bind(service);
    }
    void unbindRuntimeService(RuntimeService service) {
        runtimeService.unbind(service);
    }
}

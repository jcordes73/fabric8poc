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
package io.fabric8.test.basic.embedded;

import io.fabric8.api.Constants;
import io.fabric8.api.ManagedContainerBuilder;
import io.fabric8.api.ManagedCreateOptions;
import io.fabric8.api.container.ManagedContainer;
import io.fabric8.api.management.ContainerManagement;
import io.fabric8.api.management.ProfileManagement;
import io.fabric8.container.karaf.KarafContainerBuilder;
import io.fabric8.container.tomcat.TomcatContainerBuilder;
import io.fabric8.container.wildfly.WildFlyContainerBuilder;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.jboss.gravia.utils.MBeanProxy;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test basic {@link ManagedContainer} functionality
 *
 * @author Thomas.Diesler@jboss.com
 * @since 26-Feb-2014
 */
public class ManagedContainerTest {

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testManagedKaraf() throws Exception {
        KarafContainerBuilder builder = new KarafContainerBuilder().addIdentity("cntKaraf");
        ManagedCreateOptions options = buildCreateOptions(builder);
        ManagedContainer container = builder.getManagedContainer();
        try {
            container.create(options);
            verifyContainer(container, "karaf", "karaf");
        } finally {
            container.destroy();
        }
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testManagedTomcat() throws Exception {
        TomcatContainerBuilder builder = new TomcatContainerBuilder().addIdentity("cntTomcat");
        ManagedCreateOptions options = buildCreateOptions(builder);
        ManagedContainer container = builder.getManagedContainer();
        try {
            container.create(options);
            verifyContainer(container, null, null);
        } finally {
            container.destroy();
        }
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testManagedWildFly() throws Exception {
        WildFlyContainerBuilder builder = new WildFlyContainerBuilder().addIdentity("cntWildFly");
        ManagedCreateOptions options = buildCreateOptions(builder);
        ManagedContainer container = builder.getManagedContainer();
        try {
            container.create(options);
            verifyContainer(container, null, null);
        } finally {
            container.destroy();
        }
    }

    private ManagedCreateOptions buildCreateOptions(ManagedContainerBuilder<?, ?> builder) {
        return builder.setTargetDirectory("target/managed-container").setOutputToConsole(true).getCreateOptions();
    }

    @SuppressWarnings({ "rawtypes" })
    private ManagedContainer verifyContainer(ManagedContainer container, String jmxUsername, String jmxPassword) throws Exception {
        Assert.assertNotNull("ManagedContainer not null", container);
        File containerHome = container.getContainerHome();
        Assert.assertNotNull("Container home not null", containerHome);
        Assert.assertTrue("Container home is dir", containerHome.isDirectory());

        // Start the container
        container.start();

        JMXConnector connector = getJMXConnector(container, jmxUsername, jmxPassword, 20, TimeUnit.SECONDS);
        try {
            // Access containers through JMX
            MBeanServerConnection server = connector.getMBeanServerConnection();
            ContainerManagement cntManager = getMBeanProxy(server, ContainerManagement.OBJECT_NAME, ContainerManagement.class);
            Assert.assertNotNull("ContainerManagement not null", cntManager);

            //            Assert.assertTrue("No containers", cntManager.getContainerIds().isEmpty());

            // Access profiles through JMX
            ProfileManagement prfManager = getMBeanProxy(server, ProfileManagement.OBJECT_NAME, ProfileManagement.class);
            Assert.assertNotNull("ProfileManagement not null", prfManager);

            //            Version version = Version.parseVersion("1.0");
            //            ProfileVersion defaultVersion = prfManager.getProfileVersion(version);
            //            Assert.assertNotNull("Default profile version", defaultVersion);
            //            Assert.assertEquals(1, defaultVersion.getProfiles().size());
            //            ProfileIdentity profileIdentity = defaultVersion.getProfiles().iterator().next();
            //            Profile defaultProfile = prfManager.getProfile(version, profileIdentity);
            //            Assert.assertNotNull("Default profile", defaultProfile);
        } finally {
            connector.close();
        }
        return container;
    }

    private <T> T getMBeanProxy(MBeanServerConnection server, ObjectName oname, Class<T> type) throws IOException {
        long end = System.currentTimeMillis() + 10000L;
        while (System.currentTimeMillis() < end) {
            if (server.isRegistered(oname)) {
                return MBeanProxy.get(server, oname, type);
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                break;
            }
        }
        throw new IllegalStateException("Cannot obtain MBean proxy for: " + oname);
    }

    private JMXConnector getJMXConnector(ManagedContainer<?> container, String username, String password, long timeout, TimeUnit unit) {

        String jmxServiceURL = container.getAttribute(Constants.ATTRIBUTE_KEY_JMX_SERVER_URL);
        if (jmxServiceURL == null)
            throw new IllegalStateException("Cannot obtain container attribute: JMX_SERVER_URL");

        JMXServiceURL serviceURL;
        try {
            serviceURL = new JMXServiceURL(jmxServiceURL);
        } catch (MalformedURLException ex) {
            throw new IllegalStateException(ex);
        }

        Map<String, Object> env = new HashMap<String, Object>();
        if (username != null && password != null) {
            String[] credentials = new String[] { username, password };
            env.put(JMXConnector.CREDENTIALS, credentials);
        }

        Exception lastException = null;
        JMXConnector connector = null;
        long end = System.currentTimeMillis() + unit.toMillis(timeout);
        while (connector == null && System.currentTimeMillis() < end) {
            try {
                connector = JMXConnectorFactory.connect(serviceURL, env);
            } catch (Exception ex) {
                lastException = ex;
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ie) {
                    break;
                }
            }
        }
        if (connector == null) {
            throw new IllegalStateException("Cannot obtain JMXConnector", lastException);
        }
        return connector;
    }
}
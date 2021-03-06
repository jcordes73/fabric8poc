/*
 * #%L
 * Fabric8 :: Testsuite :: Smoke :: Common
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
package io.fabric8.test.smoke.container;

import io.fabric8.api.Container;
import io.fabric8.jolokia.JSONTypeGenerator;
import io.fabric8.spi.BootstrapComplete;
import io.fabric8.spi.utils.ManagementUtils;
import io.fabric8.test.smoke.JolokiaEndpointTestBase;
import io.fabric8.test.smoke.PrePostConditions;
import io.fabric8.test.smoke.sub.c.Bean;

import java.io.InputStream;

import javax.management.MBeanServer;
import javax.management.openmbean.CompositeData;
import javax.management.remote.JMXConnector;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.osgi.StartLevelAware;
import org.jboss.gravia.itests.support.AnnotatedContextListener;
import org.jboss.gravia.itests.support.ArchiveBuilder;
import org.jboss.gravia.resource.ManifestBuilder;
import org.jboss.gravia.resource.Resource;
import org.jboss.gravia.runtime.RuntimeLocator;
import org.jboss.gravia.runtime.RuntimeType;
import org.jboss.gravia.utils.ObjectNameFactory;
import org.jboss.osgi.metadata.OSGiManifestBuilder;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jolokia.client.J4pClient;
import org.jolokia.client.request.J4pResponse;
import org.json.simple.JSONObject;
import org.junit.runner.RunWith;

/**
 * Test the Jolokia endpoint
 *
 * @author thomas.diesler@jboss.com
 * @since 20-Jun-2014
 */
@RunWith(Arquillian.class)
public class JolokiaEndpointTest extends JolokiaEndpointTestBase {

    @Deployment
    @StartLevelAware(autostart = true)
    public static Archive<?> deployment() {
        final ArchiveBuilder archive = new ArchiveBuilder("jolokia-endpoint-test");
        archive.addClasses(RuntimeType.TOMCAT, AnnotatedContextListener.class);
        archive.addClasses(JolokiaEndpointTestBase.class, PrePostConditions.class);
        archive.addClasses(Bean.class);
        archive.setManifest(new Asset() {
            @Override
            public InputStream openStream() {
                if (ArchiveBuilder.getTargetContainer() == RuntimeType.KARAF) {
                    OSGiManifestBuilder builder = OSGiManifestBuilder.newInstance();
                    builder.addBundleManifestVersion(2);
                    builder.addBundleSymbolicName(archive.getName());
                    builder.addBundleVersion("1.0.0");
                    builder.addImportPackages(RuntimeLocator.class, Resource.class, Container.class);
                    builder.addImportPackages(ObjectNameFactory.class, ManagementUtils.class);
                    builder.addImportPackages(MBeanServer.class, CompositeData.class, JMXConnector.class);
                    builder.addImportPackages(J4pClient.class, J4pResponse.class, JSONObject.class, JSONTypeGenerator.class);
                    builder.addImportPackages(BootstrapComplete.class);
                    return builder.openStream();
                } else {
                    ManifestBuilder builder = new ManifestBuilder();
                    builder.addIdentityCapability(archive.getName(), "1.0.0");
                    builder.addManifestHeader("Dependencies", "io.fabric8.api,io.fabric8.spi,io.fabric8.jolokia,org.jboss.gravia");
                    return builder.openStream();
                }
            }
        });
        return archive.getArchive();
    }
}

/*
 * #%L
 * Fabric8 :: Testsuite :: Smoke :: Embedded
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
package io.fabric8.test.embedded.support;

import static io.fabric8.spi.RuntimeService.RUNTIME_CONF_DIR;
import static io.fabric8.spi.RuntimeService.RUNTIME_DATA_DIR;
import static io.fabric8.spi.RuntimeService.RUNTIME_HOME_DIR;
import static io.fabric8.spi.RuntimeService.RUNTIME_IDENTITY;
import io.fabric8.spi.BootstrapComplete;
import io.fabric8.spi.utils.FileUtils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.jboss.gravia.runtime.Runtime;
import org.jboss.gravia.runtime.RuntimeLocator;
import org.jboss.gravia.runtime.ServiceLocator;
import org.junit.Assert;

/**
 * Test fabric-core servies
 *
 * @author thomas.diesler@jboss.com
 * @since 21-Oct-2013
 */
public abstract class EmbeddedTestSupport {

    private static String[] moduleNames = new String[] { "gravia-provision", "gravia-resolver", "gravia-repository",
            "fabric8-api", "fabric8-spi", "fabric8-core", "fabric8-domain-agent",
            "fabric8-container-karaf-managed", "fabric8-container-tomcat-managed", "fabric8-container-wildfly-managed" };

    public static void beforeClass() throws Exception {

        Path basedir = Paths.get("").toAbsolutePath();
        Path homePath = basedir.resolve(Paths.get("target", "home"));

        System.setProperty("basedir", basedir.toString());
        System.setProperty(RUNTIME_IDENTITY, "embedded");
        System.setProperty(RUNTIME_HOME_DIR, homePath.toString());
        System.setProperty(RUNTIME_DATA_DIR, homePath.resolve("data").toString());
        System.setProperty(RUNTIME_CONF_DIR, homePath.resolve("conf").toString());

        // Delete the container's home directory - every test case starts fresh
        FileUtils.deleteRecursively(homePath);

        // Install and start the bootstrap modules
        for (String name : moduleNames) {
            ClassLoader classLoader = EmbeddedTestSupport.class.getClassLoader();
            EmbeddedUtils.installAndStartModule(classLoader, name);
        }

        // Wait for the {@link BootstrapComplete} service
        ServiceLocator.awaitService(BootstrapComplete.class, 20, TimeUnit.SECONDS);
    }

    public static void afterClass() throws Exception {
        Runtime runtime = RuntimeLocator.getRequiredRuntime();
        Assert.assertTrue(runtime.shutdown().awaitShutdown(20, TimeUnit.SECONDS));
        RuntimeLocator.releaseRuntime();
    }
}

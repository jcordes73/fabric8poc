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
package io.fabric8.test.smoke.embedded;

import io.fabric8.api.AttributeKey;
import io.fabric8.api.OptionsProvider;
import io.fabric8.api.Profile;
import io.fabric8.api.ProfileBuilder;
import io.fabric8.api.ProfileManager;
import io.fabric8.api.ProfileManagerLocator;
import io.fabric8.api.ProfileVersion;
import io.fabric8.api.VersionIdentity;
import io.fabric8.api.management.ProfileManagement;
import io.fabric8.spi.management.ProfileOpenType;
import io.fabric8.spi.utils.ManagementUtils;
import io.fabric8.test.embedded.support.EmbeddedTestSupport;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.openmbean.CompositeData;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


/**
 * Test the {@link ProfileVersion}.
 *
 * @author thomas.diesler@jboss.com
 * @since 05-Mar-2014
 */
public class ProfileOpenTypeTest {

    static AttributeKey<String> AKEY = AttributeKey.create("AKey");
    static AttributeKey<String> BKEY = AttributeKey.create("BKey");

    @BeforeClass
    public static void beforeClass() throws Exception {
        EmbeddedTestSupport.beforeClass();
    }

    @AfterClass
    public static void afterClass() throws Exception {
        EmbeddedTestSupport.afterClass();
    }

    @Test
    public void testComposisteData() throws Exception {

        ProfileBuilder profileBuilder = ProfileBuilder.Factory.create("someProfile");
        profileBuilder.addAttribute(AKEY, "AVal");
        profileBuilder.addAttribute(BKEY, "BVal");
        Profile prfA = profileBuilder.getProfile();

        ProfileManager prfManager = ProfileManagerLocator.getProfileManager();
        VersionIdentity defaultVersion = prfManager.getDefaultProfileVersion().getIdentity();
        prfA = prfManager.addProfile(defaultVersion, prfA);

        MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();
        ProfileManagement prfManagement = ManagementUtils.getMBeanProxy(mbeanServer, ProfileManagement.OBJECT_NAME, ProfileManagement.class);

        CompositeData cdata = prfManagement.getProfile("1.0", "someProfile");
        Profile prfB = ProfileOpenType.getProfile(cdata);
        Assert.assertEquals(prfA.getIdentity(), prfB.getIdentity());
        Assert.assertEquals(prfA.getAttributes(), prfB.getAttributes());

        prfManager.removeProfile(defaultVersion, prfA.getIdentity());

        // Test the {@link CompositeDataOptionsProvider}
        profileBuilder = ProfileBuilder.Factory.create();
        profileBuilder.addOptions(new CompositeDataOptionsProvider(cdata));
        Profile prfC = profileBuilder.getProfile();

        prfC = prfManager.addProfile(defaultVersion, prfC);
        Assert.assertEquals(prfA.getIdentity(), prfC.getIdentity());
        Assert.assertEquals(prfA.getAttributes(), prfC.getAttributes());

        prfManager.removeProfile(defaultVersion, prfC.getIdentity());
    }


    static class CompositeDataOptionsProvider implements OptionsProvider<ProfileBuilder> {

        private final CompositeData cdata;

        CompositeDataOptionsProvider(CompositeData cdata) {
            this.cdata = cdata;
        }

        @Override
        public ProfileBuilder addBuilderOptions(ProfileBuilder builder) {
            Profile profile = ProfileOpenType.getProfile(cdata);
            builder.identity(profile.getIdentity());
            builder.addAttributes(profile.getAttributes());
            return builder;
        }
    }
}

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

import io.fabric8.api.ConfigurationItem;
import io.fabric8.api.LinkedProfileVersion;
import io.fabric8.api.Profile;
import io.fabric8.api.ProfileBuilder;
import io.fabric8.api.ProfileIdentity;
import io.fabric8.api.ProfileManager;
import io.fabric8.api.ProfileManagerLocator;
import io.fabric8.api.ProfileVersion;
import io.fabric8.api.ProfileVersionBuilder;
import io.fabric8.api.VersionIdentity;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test {@link LinkedProfileVersion} functionality.
 *
 * @author thomas.diesler@jboss.com
 * @since 14-Mar-2014
 */
@RunWith(Arquillian.class)
public class LinkedProfileVersionTest {

    VersionIdentity version = VersionIdentity.createFrom("2.0");
    ProfileIdentity identityA = ProfileIdentity.createFrom("A");
    ProfileIdentity identityB = ProfileIdentity.createFrom("B");
    ProfileIdentity identityC = ProfileIdentity.createFrom("C");

    Map<String, Object>  configA = new HashMap<>();
    Map<String, Object>  configB = new HashMap<>();
    Map<String, Object>  configC = new HashMap<>();

    @Before
    public void setUp() {
        configA.put("keyA", "aaa");
        configA.put("keyB", "aaa");
        configA.put("keyC", "aaa");

        configB.put("keyA", "aaa");
        configB.put("keyB", "bbb");
        configB.put("keyC", "bbb");

        configC.put("keyA", "aaa");
        configC.put("keyB", "bbb");
        configC.put("keyC", "ccc");
    }

    @Test
    public void testLinkedProfileVersion() {

        Profile prfA = ProfileBuilder.Factory.create(identityA)
                .addConfigurationItem("confItem", configA)
                .addConfigurationItem("confItemA", configA)
                .getProfile();

        Profile prfB = ProfileBuilder.Factory.create(identityB)
                .addParentProfile(identityA)
                .addConfigurationItem("confItem", configB)
                .addConfigurationItem("confItemB", configB)
                .getProfile();

        Profile prfC = ProfileBuilder.Factory.create(identityC)
                .addParentProfile(identityA)
                .addParentProfile(identityB)
                .addConfigurationItem("confItem", configC)
                .addConfigurationItem("confItemC", configC)
                .getProfile();

        LinkedProfileVersion linkedVersion = ProfileVersionBuilder.Factory.create(version)
                .addProfile(prfA)
                .addProfile(prfB)
                .addProfile(prfC)
                .getProfileVersion();

        Set<ProfileIdentity> profileIdentities = linkedVersion.getProfileIdentities();
        Assert.assertEquals(3, profileIdentities.size());
        Assert.assertTrue(profileIdentities.contains(identityA));
        Assert.assertTrue(profileIdentities.contains(identityB));
        Assert.assertTrue(profileIdentities.contains(identityC));

        ProfileManager prfManager = ProfileManagerLocator.getProfileManager();
        ProfileVersion profileVersion = prfManager.addProfileVersion(linkedVersion);
        profileIdentities = profileVersion.getProfileIdentities();
        Assert.assertEquals(3, profileIdentities.size());
        Assert.assertTrue(profileIdentities.contains(identityA));
        Assert.assertTrue(profileIdentities.contains(identityB));
        Assert.assertTrue(profileIdentities.contains(identityC));

        linkedVersion = prfManager.getLinkedProfileVersion(version);
        profileIdentities = profileVersion.getProfileIdentities();
        Assert.assertEquals(3, profileIdentities.size());
        Assert.assertTrue(profileIdentities.contains(identityA));
        Assert.assertTrue(profileIdentities.contains(identityB));
        Assert.assertTrue(profileIdentities.contains(identityC));

        Profile profileA = linkedVersion.getLinkedProfile(identityA);
        Assert.assertTrue("No attributes", profileA.getAttributes().isEmpty());
        Assert.assertTrue("No parents", profileA.getParents().isEmpty());
        Assert.assertEquals(2, profileA.getProfileItems(null).size());
        Assert.assertEquals(configA, profileA.getProfileItem("confItem", ConfigurationItem.class).getDefaultAttributes());
        Assert.assertEquals(configA, profileA.getProfileItem("confItemA", ConfigurationItem.class).getDefaultAttributes());

        Profile profileB = linkedVersion.getLinkedProfile(identityB);
        Assert.assertTrue("No attributes", profileB.getAttributes().isEmpty());
        Assert.assertEquals(1, profileB.getParents().size());
        Assert.assertTrue(profileB.getParents().contains(identityA));
        Assert.assertEquals(2, profileB.getProfileItems(null).size());
        Assert.assertEquals(configB, profileB.getProfileItem("confItem", ConfigurationItem.class).getDefaultAttributes());
        Assert.assertEquals(configB, profileB.getProfileItem("confItemB", ConfigurationItem.class).getDefaultAttributes());

        Profile profileC = linkedVersion.getLinkedProfile(identityC);
        Assert.assertTrue("No attributes", profileC.getAttributes().isEmpty());
        Assert.assertEquals(2, profileC.getParents().size());
        Assert.assertTrue(profileC.getParents().contains(identityA));
        Assert.assertTrue(profileC.getParents().contains(identityB));
        Assert.assertEquals(2, profileC.getProfileItems(null).size());
        Assert.assertEquals(configC, profileC.getProfileItem("confItem", ConfigurationItem.class).getDefaultAttributes());
        Assert.assertEquals(configC, profileC.getProfileItem("confItemC", ConfigurationItem.class).getDefaultAttributes());

        prfManager.removeProfileVersion(version);
    }
}

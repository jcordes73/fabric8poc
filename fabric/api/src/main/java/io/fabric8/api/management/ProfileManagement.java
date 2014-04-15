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
package io.fabric8.api.management;

import io.fabric8.api.Constants;
import io.fabric8.api.Profile;
import io.fabric8.api.ProfileIdentity;
import io.fabric8.api.ProfileVersion;

import java.util.Set;

import javax.management.ObjectName;

import org.jboss.gravia.resource.Version;
import org.jboss.gravia.utils.ObjectNameFactory;

/**
 * The profile management interface
 *
 * @author Thomas.Diesler@jboss.com
 * @since 10-Apr-2014
 */
public interface ProfileManagement {

    /**
     * The ObjectName: fabric8:type=ProfileManagement
     */
    ObjectName OBJECT_NAME = ObjectNameFactory.create(Constants.MANAGEMENT_DOMAIN + ":type=" + ProfileManagement.class.getSimpleName());

    /**
     * Get the set of profile version identities in the cluster
     */
    Set<Version> getProfileVersionIds();

    /**
     * Get the profile versions for the given identity
     */
    ProfileVersion getProfileVersion(Version identity);

    /**
     * Get the profile idetities for a given version
     */
    Set<ProfileIdentity> getProfileIds(Version version);

    /**
     * Get the profile for a given identity and version
     */
    Profile getProfile(Version version, ProfileIdentity identity);
}
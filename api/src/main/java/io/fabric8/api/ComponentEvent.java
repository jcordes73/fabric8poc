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
package io.fabric8.api;


/**
 * A component event
 *
 * @author Thomas.Diesler@jboss.com
 * @since 14-Mar-2014
 */
@SuppressWarnings("serial")
public class ComponentEvent extends FabricEvent<String, ComponentEvent.EventType> {

    public enum EventType {
        ACTIVATING, ACTIVATED, DEACTIVATING, DEACTIVATED, ERROR
    }

    public ComponentEvent(String serviceName, EventType type) {
        this(serviceName, type, null);
    }

    public ComponentEvent(String serviceName, EventType type, Throwable error) {
        super(serviceName, type, error);
    }
}
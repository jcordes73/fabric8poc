package io.fabric8.spi;
/*
 * #%L
 * Gravia :: Repository
 * %%
 * Copyright (C) 2012 - 2014 JBoss by Red Hat
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

import io.fabric8.api.LinkedProfileVersion;

import java.io.Closeable;
import java.io.IOException;

/**
 * Write repository contnet.
 *
 * @author thomas.diesler@jboss.com
 * @since 11-Jun-2014
 */
public interface ProfileWriter extends Closeable {

    void writeProfileVersion(LinkedProfileVersion profileVersion) throws IOException;
}

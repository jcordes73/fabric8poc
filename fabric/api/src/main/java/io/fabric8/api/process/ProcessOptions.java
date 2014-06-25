/*
 * #%L
 * Fabric8 :: SPI
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
package io.fabric8.api.process;

import io.fabric8.api.Attributable;

import java.net.InetAddress;
import java.nio.file.Path;
import java.util.List;

import org.jboss.gravia.resource.MavenCoordinates;
import org.jboss.gravia.runtime.RuntimeType;

/**
 * Process create options
 *
 * @author thomas.diesler@jboss.com
 * @since 14-Mar-2014
 */
public interface ProcessOptions extends Attributable {

    String getIdentityPrefix();

    RuntimeType getRuntimeType();

    InetAddress getTargetHost();

    List<MavenCoordinates> getMavenCoordinates();

    Path getTargetPath();

    String getJavaVmArguments();

    boolean isOutputToConsole();
}

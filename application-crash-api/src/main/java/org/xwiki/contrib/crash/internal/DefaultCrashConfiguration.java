/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.contrib.crash.internal;

import javax.inject.Inject;

import org.xwiki.component.annotation.Component;
import org.xwiki.configuration.ConfigurationSource;
import org.xwiki.contrib.crash.CrashConfiguration;

@Component
public class DefaultCrashConfiguration implements CrashConfiguration
{
    private static final String PREFIX = "crash.";

    private static final String SSH_PREFIX = PREFIX + "ssh.";

    @Inject
    public ConfigurationSource configurationSource;

    @Override public int getSSHPort()
    {
        return this.configurationSource.getProperty(SSH_PREFIX + "port", 2000);
    }

    @Override
    public String getAuthentication()
    {
        return this.configurationSource.getProperty(SSH_PREFIX + "auth", "all");
    }

    @Override public String getSSHUserName()
    {
        return this.configurationSource.getProperty(SSH_PREFIX + "auth.simple.username");
    }

    @Override public String getSSHPassword()
    {
        return this.configurationSource.getProperty(SSH_PREFIX + "auth.simple.password");
    }

    @Override public String getSSHKeyLocation()
    {
        return this.configurationSource.getProperty(SSH_PREFIX + "auth.key.location");
    }
}

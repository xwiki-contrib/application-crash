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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.Properties;

import org.crsh.plugin.PluginContext;
import org.crsh.plugin.PluginLifeCycle;
import org.crsh.plugin.ServiceLoaderDiscovery;
import org.crsh.vfs.FS;
import org.crsh.vfs.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XWikiPluginLifecycle extends PluginLifeCycle
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XWikiPluginLifecycle.class);

    /**
     * The base classloader.
     */
    private final ClassLoader loader;

    /**
     * The command file system.
     */
    private final FS cmdFS = new FS();

    /**
     * The configuration file system.
     */
    private final FS confFS = new FS();

    private XWikiComponentReferences componentReferences;

    private PluginContext pluginContext;

    public XWikiPluginLifecycle(ClassLoader baseLoader, XWikiComponentReferences componentReferences)
    {
        this.loader = baseLoader;
        this.componentReferences = componentReferences;
    }

    public void start()
    {
        try {
            startInternal();
        } catch (Exception e) {
            LOGGER.error("Failed to initialize CRaSH", e);
        }
    }

    private void startInternal() throws URISyntaxException, IOException
    {
        // The service loader discovery
        ServiceLoaderDiscovery discovery = new ServiceLoaderDiscovery(this.loader);

        PluginContext pluginContext =
            new PluginContext(discovery, Collections.EMPTY_MAP, this.cmdFS, this.confFS, this.loader);

        Properties props = new Properties();

        // Ensure that CRaSH doesn't automatically update its list of commands. We want to do that manually whenever a
        // wiki page is modified, see CrashEventListener.
        props.setProperty("crash.vfs.refresh_period", "0");

        props.setProperty("crash.ssh.port", "" + this.componentReferences.configuration.getSSHPort());

        // TODO: Plug onto XWiki's auth
        props.setProperty("crash.auth", "simple");
        String username = this.componentReferences.configuration.getSSHUserName();
        if (username != null) {
            props.setProperty("crash.auth.simple.username", username);
        }
        String password = this.componentReferences.configuration.getSSHPassword();
        if (password != null) {
            props.setProperty("crash.auth.simple.password", password);
        }

        // Register our configuration
        setConfig(props);

        // Mount default commands
        this.cmdFS.mount(this.loader, Path.get("/crash/commands/"));

        // Mount default configurations
        this.confFS.mount(this.loader, Path.get("/crash/"));

        // Mount the XWiki FS Driver
        XWikiFSDriver driver = new XWikiFSDriver(this.componentReferences);
        this.cmdFS.mount(driver);

        this.pluginContext = pluginContext;

        pluginContext.refresh();

        start(pluginContext);
    }

    public void refresh()
    {
        if (this.pluginContext != null) {
            this.pluginContext.refresh();
        }
    }
}

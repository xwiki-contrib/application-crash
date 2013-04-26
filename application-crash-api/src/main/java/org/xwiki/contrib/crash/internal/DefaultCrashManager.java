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
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.context.Execution;
import org.xwiki.context.ExecutionContextManager;
import org.xwiki.contrib.crash.CrashConfiguration;
import org.xwiki.contrib.crash.CrashManager;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.model.reference.EntityReferenceValueProvider;
import org.xwiki.query.QueryManager;
import org.xwiki.script.service.ScriptServiceManager;

import com.xpn.xwiki.util.XWikiStubContextProvider;

@Component
@Singleton
public class DefaultCrashManager implements CrashManager
{
    @Inject
    private CrashConfiguration configuration;

    @Inject
    private QueryManager queryManager;

    @Inject
    @Named("default")
    private DocumentReferenceResolver<String> defaultReferenceResolver;

    @Inject
    @Named("local")
    private EntityReferenceSerializer<String> referenceSerializer;

    @Inject
    private Execution execution;

    @Inject
    private ExecutionContextManager executionContextManager;

    @Inject
    private XWikiStubContextProvider stubContextProvider;

    @Inject
    private EntityReferenceValueProvider defaultEntityReferenceValueProvider;

    @Inject
    private ScriptServiceManager scriptServiceManager;

    @Inject
    private ComponentManager componentManager;

    private XWikiPluginLifecycle lifecycle;

    @Override
    public void start()
    {
        // Protects against several calls to start()
        if (!isStarted()) {
            // Initialize Crash
            XWikiComponentReferences componentReferences = new XWikiComponentReferences();
            componentReferences.configuration = this.configuration;
            componentReferences.defaultEntityReferenceValueProvider = this.defaultEntityReferenceValueProvider;
            componentReferences.execution = this.execution;
            componentReferences.executionContextManager = this.executionContextManager;
            componentReferences.queryManager = this.queryManager;
            componentReferences.referenceResolver = this.defaultReferenceResolver;
            componentReferences.referenceSerializer = this.referenceSerializer;
            componentReferences.stubContextProvider = this.stubContextProvider;
            componentReferences.scriptServiceManager = this.scriptServiceManager;
            componentReferences.componentManager = this.componentManager;

            this.lifecycle =
                new XWikiPluginLifecycle(Thread.currentThread().getContextClassLoader(), componentReferences);
            this.lifecycle.start();
        }
    }

    @Override
    public void refresh()
    {
        if (isStarted()) {
            this.lifecycle.refresh();
        }
    }

    @Override
    public void stop()
    {
        // Protects against several calls to stop()
        if (isStarted()) {
            this.lifecycle.stop();
            this.lifecycle = null;
        }
    }

    @Override
    public boolean isStarted()
    {
         return this.lifecycle != null;
    }
}

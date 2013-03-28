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

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.xwiki.bridge.event.ApplicationReadyEvent;
import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLifecycleException;
import org.xwiki.component.phase.Disposable;
import org.xwiki.context.Execution;
import org.xwiki.context.ExecutionContextManager;
import org.xwiki.contrib.crash.CrashConfiguration;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.model.reference.EntityReferenceValueProvider;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;
import org.xwiki.query.QueryManager;

import com.xpn.xwiki.util.XWikiStubContextProvider;

@Component
@Named("Crash")
@Singleton
public class CrashEventListener implements EventListener, Disposable
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

    private XWikiPluginLifecycle lifecycle;

    @Override
    public String getName()
    {
        return "Crash";
    }

    @Override
    public List<Event> getEvents()
    {
        // Note that we listen to the Application Ready event and not the Applicatjon Started event since the
        // Crash init may call our dynamic FSDriver implementation and it requires querying the wiki for locating
        // Crash commands that may have been add to wiki pages.
        return Arrays.<Event>asList(new ApplicationReadyEvent());
    }

    @Override
    public void onEvent(Event event, Object source, Object data)
    {
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

        this.lifecycle =
            new XWikiPluginLifecycle(Thread.currentThread().getContextClassLoader(), componentReferences);
        this.lifecycle.start();
    }

    @Override
    public void dispose() throws ComponentLifecycleException
    {
        this.lifecycle.stop();
    }
}

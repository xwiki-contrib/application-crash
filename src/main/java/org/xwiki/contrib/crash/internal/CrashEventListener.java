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

import org.xwiki.component.annotation.Component;
import org.xwiki.component.manager.ComponentLifecycleException;
import org.xwiki.component.phase.Disposable;
import org.xwiki.contrib.crash.CrashConfiguration;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.ApplicationStartedEvent;
import org.xwiki.observation.event.Event;
import org.xwiki.query.QueryManager;

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
    @Named("current")
    private DocumentReferenceResolver<String> currentReferenceResolver;

    @Inject
    @Named("local")
    private EntityReferenceSerializer<String> referenceSerializer;

    private XWikiPluginLifecycle lifecycle;

    @Override
    public String getName()
    {
        return "Crash";
    }

    @Override
    public List<Event> getEvents()
    {
        return Arrays.<Event>asList(new ApplicationStartedEvent());
    }

    @Override
    public void onEvent(Event event, Object source, Object data)
    {
        // Initialize Crash
        this.lifecycle =
            new XWikiPluginLifecycle(Thread.currentThread().getContextClassLoader(), this.configuration,
                this.queryManager, this.currentReferenceResolver, this.referenceSerializer);
        this.lifecycle.start();
    }

    @Override
    public void dispose() throws ComponentLifecycleException
    {
        this.lifecycle.stop();
    }
}

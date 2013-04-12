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

import org.xwiki.bridge.event.DocumentCreatedEvent;
import org.xwiki.bridge.event.DocumentDeletedEvent;
import org.xwiki.bridge.event.DocumentUpdatedEvent;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.crash.CrashManager;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.EntityReference;
import org.xwiki.observation.EventListener;
import org.xwiki.observation.event.Event;

import com.xpn.xwiki.doc.XWikiDocument;
import com.xpn.xwiki.objects.BaseObject;

@Component
@Named("crash")
@Singleton
public class CrashEventListener implements EventListener
{
    @Inject
    private CrashManager crashManager;

    /**
     * Constant for representing Crash.CrashCommandClass xwiki class which is the CRaSH Command Class.
     */
    EntityReference CRASH_COMMAND_CLASS = new EntityReference("CrashCommandClass", EntityType.DOCUMENT,
        new EntityReference("Crash", EntityType.SPACE));

    @Override
    public String getName()
    {
        return "Crash";
    }

    @Override
    public List<Event> getEvents()
    {
        return Arrays.<Event>asList(new DocumentCreatedEvent(), new DocumentUpdatedEvent(), new DocumentDeletedEvent());
    }

    @Override
    public void onEvent(Event event, Object source, Object data)
    {
        if (source instanceof XWikiDocument) {
            XWikiDocument document = (XWikiDocument) source;
            BaseObject configurationObject = document.getXObject(CRASH_COMMAND_CLASS);
            if (configurationObject != null) {
                // A Crash Command has been modified/created/deleted, tell CRaSH to refresh its command list
                this.crashManager.refresh();
            }
        }
    }
}

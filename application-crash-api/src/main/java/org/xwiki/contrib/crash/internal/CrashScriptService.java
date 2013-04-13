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
import org.xwiki.context.Execution;
import org.xwiki.contrib.crash.CrashManager;
import org.xwiki.script.service.ScriptService;

import com.xpn.xwiki.XWikiContext;

@Component
@Named("crash")
@Singleton
public class CrashScriptService implements ScriptService
{
    @Inject
    private CrashManager crashManager;

    @Inject
    private Execution execution;

    public void start()
    {
        if (hasPermissions()) {
            this.crashManager.start();
        }
    }

    public void stop()
    {
        if (hasPermissions()) {
            this.crashManager.stop();
        }
    }

    public boolean isStarted()
    {
        return this.crashManager.isStarted();
    }

    private boolean hasPermissions()
    {
        XWikiContext xwikiContext = getXWikiContext();
        return xwikiContext.isMainWiki() && xwikiContext.getWiki().getRightService().hasAdminRights(xwikiContext);
    }

    private XWikiContext getXWikiContext()
    {
        return (XWikiContext) this.execution.getContext().getProperty(XWikiContext.EXECUTIONCONTEXT_KEY);
    }
}

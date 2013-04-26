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
package org.xwiki.contrib.crash.internal.authentication;

import java.security.Principal;

import javax.inject.Named;
import javax.inject.Singleton;

import org.crsh.plugin.PluginContext;
import org.xwiki.component.annotation.Component;
import org.xwiki.contrib.crash.CrashAuthentication;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.XWikiException;

@Component
@Named("xwiki")
@Singleton
public class XWikiCrashAuthentication implements CrashAuthentication
{
    @Override
    public boolean authenticate(String username, String password, PluginContext pluginContext)
    {
        boolean isAuthenticated = false;
        XWikiContext xwikiContext = (XWikiContext) pluginContext.getAttributes().get("xcontext");
        try {
            Principal principal =
                xwikiContext.getWiki().getAuthService().authenticate(username, password, xwikiContext);
            isAuthenticated = principal != null;
        } catch (XWikiException e) {
            // Nothing to do, isAuthenticated is false by default
        }
        return isAuthenticated;
    }
}

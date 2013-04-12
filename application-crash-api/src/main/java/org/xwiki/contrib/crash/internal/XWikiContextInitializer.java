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

import org.xwiki.context.ExecutionContext;
import org.xwiki.context.ExecutionContextException;
import org.xwiki.model.EntityType;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.web.XWikiURLFactory;

public class XWikiContextInitializer
{
    public void initalize(XWikiComponentReferences componentReferences)
    {
        ExecutionContext context = componentReferences.execution.getContext();
        if (context == null) {
            // Create a clean Execution Context
            context = new ExecutionContext();

            try {
                componentReferences.executionContextManager.initialize(context);
            } catch (ExecutionContextException e) {
                throw new RuntimeException("Failed to initialize IRC Bot's execution context", e);
            }

            // Bridge with old XWiki Context, required for old code.
            XWikiContext xwikiContext = componentReferences.stubContextProvider.createStubContext();
            context.setProperty(XWikiContext.EXECUTIONCONTEXT_KEY, xwikiContext);

            // Ensure that the Servlet URL Factory is used since the Notifications Event Listener needs to compute
            // External URLs (for example).
            XWikiURLFactory urlf = xwikiContext.getWiki().getURLFactoryService().createURLFactory(
                XWikiContext.MODE_SERVLET, xwikiContext);
            xwikiContext.setURLFactory(urlf);

            // Set the current wiki
            xwikiContext.setDatabase(componentReferences.defaultEntityReferenceValueProvider.getDefaultValue(
                EntityType.WIKI));

            componentReferences.execution.pushContext(context);
        }
    }
}

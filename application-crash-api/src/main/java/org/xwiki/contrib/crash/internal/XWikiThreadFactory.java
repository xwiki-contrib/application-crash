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

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * A thread factory that ensures that created threads have the XWiki Execution Context properly initialized.
 *
 * @version $Id$
 * @since 1.1
 */
public class XWikiThreadFactory implements ThreadFactory
{
    final ThreadFactory factory = Executors.defaultThreadFactory();
    final XWikiComponentReferences componentReferences;
    final XWikiContextInitializer initializer = new XWikiContextInitializer();

    public XWikiThreadFactory(XWikiComponentReferences componentReferences)
    {
        this.componentReferences = componentReferences;
    }

    @Override
    public Thread newThread(final Runnable runnable)
    {
        return factory.newThread(new Runnable()
        {
            @Override
            public void run()
            {
                initializer.initalize(componentReferences);
                runnable.run();
            }
        });
    }
}

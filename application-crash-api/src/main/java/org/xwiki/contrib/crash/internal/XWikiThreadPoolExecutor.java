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

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Extend {@link ThreadPoolExecutor} in order to be able to provide a special {@link Callable} that will set up the
 * XWiki Context prior to delegating to the original Callable called by CRaSH which is used to execute a CRaSH command.
 *
 * @version $Id$
 */
public class XWikiThreadPoolExecutor extends ThreadPoolExecutor
{
    private XWikiComponentReferences componentReferences;

    public XWikiThreadPoolExecutor(XWikiComponentReferences componentReferences)
    {
        super(2, 2, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        this.componentReferences = componentReferences;
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> tCallable)
    {
        return super.newTaskFor(new XWikiCallable<T>(tCallable, componentReferences));
    }
}

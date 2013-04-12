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

public class XWikiCallable<T> implements Callable<T>
{
    private Callable<T> delegate;

    private XWikiComponentReferences componentReferences;

    private XWikiContextInitializer initializer = new XWikiContextInitializer();

    public XWikiCallable(Callable<T> delegate, XWikiComponentReferences componentReferences)
    {
        this.delegate = delegate;
        this.componentReferences = componentReferences;
    }

    @Override
    public T call() throws Exception
    {
        this.initializer.initalize(this.componentReferences);
        return this.delegate.call();
    }
}

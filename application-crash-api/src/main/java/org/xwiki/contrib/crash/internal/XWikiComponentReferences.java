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

import org.xwiki.context.Execution;
import org.xwiki.context.ExecutionContextManager;
import org.xwiki.contrib.crash.CrashConfiguration;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.model.reference.EntityReferenceValueProvider;
import org.xwiki.query.QueryManager;
import org.xwiki.script.service.ScriptServiceManager;

import com.xpn.xwiki.util.XWikiStubContextProvider;

public class XWikiComponentReferences
{
    public CrashConfiguration configuration;

    public QueryManager queryManager;

    public DocumentReferenceResolver<String> referenceResolver;

    public EntityReferenceSerializer<String> referenceSerializer;

    public Execution execution;

    public ExecutionContextManager executionContextManager;

    public XWikiStubContextProvider stubContextProvider;

    public EntityReferenceValueProvider defaultEntityReferenceValueProvider;

    public ScriptServiceManager scriptServiceManager;
}

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.crsh.vfs.spi.AbstractFSDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.context.ExecutionContext;
import org.xwiki.context.ExecutionContextException;
import org.xwiki.model.EntityType;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.web.XWikiURLFactory;

public class XWikiFSDriver extends AbstractFSDriver<DocumentReference>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XWikiFSDriver.class);

    private XWikiComponentReferences componentReferences;

    private static final DocumentReference ROOT = new DocumentReference("crash", "crash", "crash");

    public XWikiFSDriver(XWikiComponentReferences componentReferences)
    {
        this.componentReferences = componentReferences;
    }

    @Override public DocumentReference root() throws IOException
    {
        return ROOT;
    }

    @Override public String name(DocumentReference handle) throws IOException
    {
        String result;

        if (handle.equals(ROOT)) {
            result = "Root";
        } else {
            initializeXWikiContext();
            try {
                Query query = this.componentReferences.queryManager.createQuery(
                    "select distinct crashCommand.name from Document doc, doc.object(Crash.CrashCommandClass) as crashCommand where doc.fullName = :docName",
                    Query.XWQL);
                query.bindValue("docName", this.componentReferences.referenceSerializer.serialize(handle));
                List<String> names = query.execute();
                result = names.get(0) + ".groovy";
            } catch (QueryException e) {
                throw new IOException(String.format("Failed to get Crash command for [%s]", handle), e);
            }
        }

        return result;
    }

    @Override public boolean isDir(DocumentReference handle) throws IOException
    {
        return handle.equals(ROOT);
    }

    @Override public Iterable<DocumentReference> children(DocumentReference handle) throws IOException
    {
        Iterable<DocumentReference> result;
        initializeXWikiContext();
        try {
            Query query = this.componentReferences.queryManager.createQuery("from doc.object(Crash.CrashCommandClass) as crashCommand",
                Query.XWQL);
            List<String> stringDocumentReferences = query.execute();
            List<DocumentReference> documentReferences = new ArrayList<DocumentReference>();
            for (String documentReference : stringDocumentReferences) {
                documentReferences.add(this.componentReferences.referenceResolver.resolve(documentReference));
            }
            result = documentReferences;
        } catch (QueryException e) {
            throw new IOException(String.format("Failed to get children for [%s]", handle), e);
        }

        return result;
    }

    @Override public long getLastModified(DocumentReference handle) throws IOException
    {
        long lastModifiedDate;
        initializeXWikiContext();
        try {
            Query query = this.componentReferences.queryManager.createQuery("select distinct doc.date from Document doc, doc.object(Crash.CrashCommandClass) as crashCommand where doc.fullName = :docName",
                Query.XWQL);
            query.bindValue("docName", this.componentReferences.referenceSerializer.serialize(handle));
            List<Date> lastModifiedDates = query.execute();
            lastModifiedDate = lastModifiedDates.get(0).getTime();
        } catch (QueryException e) {
            throw new IOException(String.format("Failed to get last modified document date for [%s]", handle), e);
        }

        return lastModifiedDate;
    }

    @Override public InputStream open(DocumentReference handle) throws IOException
    {
        InputStream result;

        initializeXWikiContext();
        try {
            Query query = this.componentReferences.queryManager.createQuery(
                "select distinct crashCommand.command from Document doc, doc.object(Crash.CrashCommandClass) as crashCommand where doc.fullName = :docName",
                Query.XWQL);
            query.bindValue("docName", this.componentReferences.referenceSerializer.serialize(handle));
            List<String> commands = query.execute();
            result = new ByteArrayInputStream(commands.get(0).getBytes());
        } catch (QueryException e) {
            throw new IOException(String.format("Failed to get Crash command for [%s]", handle), e);
        }
        return result;
    }

    private void initializeXWikiContext()
    {
        ExecutionContext context = this.componentReferences.execution.getContext();
        if (context == null) {
            // Create a clean Execution Context
            context = new ExecutionContext();

            try {
                this.componentReferences.executionContextManager.initialize(context);
            } catch (ExecutionContextException e) {
                throw new RuntimeException("Failed to initialize IRC Bot's execution context", e);
            }

            // Bridge with old XWiki Context, required for old code.
            XWikiContext xwikiContext = this.componentReferences.stubContextProvider.createStubContext();
            context.setProperty(XWikiContext.EXECUTIONCONTEXT_KEY, xwikiContext);

            // Ensure that the Servlet URL Factory is used since the Notifications Event Listener needs to compute
            // External URLs (for example).
            XWikiURLFactory urlf = xwikiContext.getWiki().getURLFactoryService().createURLFactory(
                XWikiContext.MODE_SERVLET, xwikiContext);
            xwikiContext.setURLFactory(urlf);

            // Set the current wiki
            xwikiContext.setDatabase(this.componentReferences.defaultEntityReferenceValueProvider.getDefaultValue(
                EntityType.WIKI));

            this.componentReferences.execution.pushContext(context);
        }
    }
}

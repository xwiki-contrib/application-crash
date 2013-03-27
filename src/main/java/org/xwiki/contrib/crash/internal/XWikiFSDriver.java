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
import java.util.List;

import org.crsh.vfs.spi.AbstractFSDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xwiki.model.reference.DocumentReference;
import org.xwiki.model.reference.DocumentReferenceResolver;
import org.xwiki.model.reference.EntityReferenceSerializer;
import org.xwiki.query.Query;
import org.xwiki.query.QueryException;
import org.xwiki.query.QueryManager;

public class XWikiFSDriver extends AbstractFSDriver<DocumentReference>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(XWikiFSDriver.class);

    private DocumentReferenceResolver<String> referenceResolver;

    private EntityReferenceSerializer<String> referenceSerializer;

    private QueryManager queryManager;

    public XWikiFSDriver(QueryManager queryManager, DocumentReferenceResolver<String> referenceResolver,
        EntityReferenceSerializer<String> referenceSerializer)
    {
        this.queryManager = queryManager;
        this.referenceResolver = referenceResolver;
        this.referenceSerializer = referenceSerializer;
    }

    @Override public DocumentReference root() throws IOException
    {
        return null;
    }

    @Override public String name(DocumentReference handle) throws IOException
    {
        String result;

        if (handle == null) {
            result = "Root";
        } else {
            result = handle.toString();
        }

        return result;
    }

    @Override public boolean isDir(DocumentReference handle) throws IOException
    {
        return handle == null;
    }

    @Override public Iterable<DocumentReference> children(DocumentReference handle) throws IOException
    {
        Iterable<DocumentReference> result;
        try {
            Query query = this.queryManager.createQuery("from doc.object(CRaSH.CRaSHClass) as crashCommand",
                Query.XWQL);
            List<String> stringDocumentReferences = query.execute();
            List<DocumentReference> documentReferences = new ArrayList<DocumentReference>();
            for (String documentReference : stringDocumentReferences) {
                documentReferences.add(this.referenceResolver.resolve(documentReference));
            }
            result = documentReferences;
        } catch (QueryException e) {
            throw new IOException(String.format("Failed to get children for [%s]", handle), e);
        }

        return result;
    }

    @Override public long getLastModified(DocumentReference handle) throws IOException
    {
        return 0;
    }

    @Override public InputStream open(DocumentReference handle) throws IOException
    {
        InputStream result;

        try {
            Query query = this.queryManager.createQuery(
                "select distinct crashCommand.command from Document doc, doc.object(CRaSH.CRaSHClass) as crashCommand where doc.fullName = :docName",
                Query.XWQL);
            query.bindValue("docName", this.referenceSerializer.serialize(handle));
            List<String> commands = query.execute();
            result = new ByteArrayInputStream(commands.get(0).getBytes());
        } catch (QueryException e) {
            throw new IOException(String.format("Failed to get Crash command for [%s]", handle), e);
        }
        return result;
    }
}

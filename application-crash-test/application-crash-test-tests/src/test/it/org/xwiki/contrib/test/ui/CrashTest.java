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
package org.xwiki.contrib.test.ui;

import java.io.ByteArrayOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

import org.apache.sshd.ClientChannel;
import org.apache.sshd.ClientSession;
import org.apache.sshd.SshClient;
import org.junit.Assert;
import org.junit.Test;
import org.xwiki.contrib.crash.test.po.CrashAdminPage;
import org.xwiki.test.ui.AbstractAdminAuthenticatedTest;

/**
 * UI tests for the Crash application.
 *
 * @version $Id$
 */
public class CrashTest extends AbstractAdminAuthenticatedTest
{
    @Test
    public void connectWithCrashSSHAndCallXWikiCommand() throws Exception
    {
        CrashAdminPage cap = CrashAdminPage.gotoPage();
        Assert.assertEquals("CRaSH has been started!", cap.start());

        SshClient client = SshClient.setUpDefaultClient();
        client.start();
        ClientSession session = client.connect("localhost", 2000).await().getSession();
        session.authPassword("Admin", "admin").await().isSuccess();

        ClientChannel channel = session.createChannel(ClientChannel.CHANNEL_SHELL);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ByteArrayOutputStream sent = new ByteArrayOutputStream();
            PipedOutputStream pipedIn = new TeePipedOutputStream(sent);
            channel.setIn(new PipedInputStream(pipedIn));
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            channel.setOut(out);
            channel.setErr(err);
            channel.open();

            // Send "help" command to verify that the default "xwiki" command is listed
            pipedIn.write("help\n".getBytes());
            pipedIn.flush();

            // Execute the "xwiki" command to get all users defined in the wiki
            pipedIn.write("xwiki xwql \"from doc.object(XWiki.XWikiUsers) as user\"\n".getBytes());
            pipedIn.flush();

            // Close the ssh client
            pipedIn.write("exit\n".getBytes());
            pipedIn.flush();
        } finally {
            channel.waitFor(ClientChannel.CLOSED, 0);
            channel.close(false);
            client.stop();
        }

        // Verify what's been sent to the output stream
        String result = out.toString();
        Assert.assertTrue(result.contains("[31mxwiki    \u001B[39m XWiki commands"));
        Assert.assertTrue(result.contains("- XWiki.Admin"));
    }
}

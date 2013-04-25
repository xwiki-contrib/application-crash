package org.xwiki.contrib.crash.internal;

import java.security.Principal;

import com.xpn.xwiki.XWikiContext;
import org.crsh.auth.AuthenticationPlugin;
import org.crsh.plugin.CRaSHPlugin;

public class XWikiAuthenticationPlugin extends CRaSHPlugin<AuthenticationPlugin> implements AuthenticationPlugin {

    @Override
    public String getName() {
        return "xWikiAuthentication";
    }

    @Override
    public boolean authenticate(String username, String password) throws Exception {
        XWikiContext xWikiContext = (XWikiContext) getContext().getAttributes().get("xWikiContext");
        Principal principal = xWikiContext.getWiki().getAuthService().authenticate(username, password, xWikiContext);
        return principal != null;
    }

    @Override
    public AuthenticationPlugin getImplementation() {
        return this;
    }
}

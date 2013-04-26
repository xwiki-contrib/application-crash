package org.xwiki.contrib.crash.internal.authentication;

import org.crsh.auth.AuthenticationPlugin;
import org.crsh.plugin.CRaSHPlugin;
import org.xwiki.component.manager.ComponentManager;
import org.xwiki.contrib.crash.CrashAuthentication;
import org.xwiki.contrib.crash.CrashConfiguration;

public class XWikiAuthenticationPlugin extends CRaSHPlugin<AuthenticationPlugin> implements AuthenticationPlugin
{
    @Override
    public String getName()
    {
        return "XWikiAuthentication";
    }

    @Override
    public boolean authenticate(String username, String password) throws Exception
    {
        boolean isAuthenticated = false;
        ComponentManager componentManager = (ComponentManager) getContext().getAttributes().get("componentManager");
        try {
            CrashConfiguration configuration = componentManager.getInstance(CrashConfiguration.class);
            CrashAuthentication authentication =
                componentManager.getInstance(CrashAuthentication.class, configuration.getAuthentication());
            isAuthenticated = authentication.authenticate(username, password, getContext());
        } catch (Exception e) {
            // Nothing to do, isAuthenticated is false by default
        }
        return isAuthenticated;
    }

    @Override
    public AuthenticationPlugin getImplementation()
    {
        return this;
    }
}

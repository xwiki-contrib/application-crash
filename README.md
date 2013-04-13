Note: This extension is not yet released. When it will be it'll be available with instructions on http://extensions.xwiki.org

Usage
=====

* Set up the XWiki Maven Repository in your `settings.xml`
  (see [Building XWiki](http://dev.xwiki.org/xwiki/bin/view/Community/Building#HInstallingMaven))
* Build with `mvn clean install -Pintegration-tests`
* The `integration-tests` profile will create a minimal XWiki distribution used for the functional tests but which you
  can use to test the CRaSH integration too:
    * Navigate to `application-crash-test/application-crash-test-tests/target/xwiki`
    * Start XWiki: `sh start_xwiki.sh`
    * Login as `Admin` (password: `admin`)
    * Open your browser and go to the CRaSH Admin UI to start CRaSH.
      Direct link: http://localhost:8080/xwiki/bin/admin/XWiki/XWikiPreferences?editor=globaladmin&section=CRaSH
    * In a different shell, connect with SSH: `ssh admin@localhost -p 2000` and log in as `admin`\`admin`
    * Type help and verify that you see an `xwiki` command
    * Try: `xwiki xwql "from doc.object(XWiki.XWikiUsers) as user"` It should return the list of all users in your
      wiki (you should see only the Admin user returned)

Misc
====

There's an existing integration of CRaSH into XWiki available here: http://extensions.xwiki.org/xwiki/bin/view/Extension/CRaSH+Console

The purpose of this new version is to:
* Use a new version of CRaSH
* Use a different way to integrate it (as suggested by its creator Julien Viet)
* Focus initially on just providing a SSH access to the CRaSH console
* Allow adding new CRaSH commands in wiki pages

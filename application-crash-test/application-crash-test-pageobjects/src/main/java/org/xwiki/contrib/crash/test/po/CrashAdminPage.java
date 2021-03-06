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
package org.xwiki.contrib.crash.test.po;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.xwiki.administration.test.po.AdministrationSectionPage;

public class CrashAdminPage extends AdministrationSectionPage
{
    @FindBy(id = "crashStart")
    private WebElement startInput;

    @FindBy(id = "crashStop")
    private WebElement stopInput;

    @FindBy(xpath = "//form/fieldset/span/input[@type = 'submit']")
    private WebElement submitInput;

    public CrashAdminPage()
    {
        super("CRaSH");
    }

    public static CrashAdminPage gotoPage()
    {
        CrashAdminPage page = new CrashAdminPage();
        page.getDriver().get(page.getURL());

        return page;
    }

    public String start()
    {
        this.startInput.click();
        this.submitInput.click();
        return getMessage();
    }

    public String stop()
    {
        this.stopInput.click();
        this.submitInput.click();
        return getMessage();
    }

    private String getMessage()
    {
        return getDriver().findElement(
            By.xpath("//div[contains(@class, 'codeToExecute')]/div[contains(@class, 'box')]")).getText();
    }
}

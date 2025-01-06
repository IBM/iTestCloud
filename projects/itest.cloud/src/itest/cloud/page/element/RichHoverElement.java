/*********************************************************************
 * Copyright (c) 2012, 2022 IBM Corporation and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *********************************************************************/
package itest.cloud.page.element;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;

import itest.cloud.page.Page;

/**
 * Abstract class for any window opened as a rich hover in a browser page.
 * <p>
 * Following functionalities are specialized by the rich hover:
 * <ul>
 * </ul>
* </p><p>
 * Following operations are also defined or specialized for rich hovers:
 * <ul>
 * <li>{@link #closeAction(boolean)}: The action to perform to close the window.</li>
 * <li>{@link #getCloseButton(boolean)}: The button to close the hover.</li>
 * <li>{@link #getTitleLinkXpath()}: Return the xpath for the the title link element.</li>
 * </ul>
  * </p>
 */
abstract public class RichHoverElement<P extends Page> extends LinkHoverElement<P> {

public RichHoverElement(final Page page) {
	super(page);
}

/**
 * The action to perform to close the window.
 * <p>
 * A dialog is closed by clicking on the "Close" button (see {@link #getCloseButton(boolean)})
 * </p>
 */
@Override
protected void closeAction(final boolean cancel) {

	// Get the close link element
	switchToMainWindow();
	BrowserElement closeLinkElement = this.browser.findElement(By.xpath(getCloseButton(cancel)), false/*no recovery*/);

	// Click on the link element
	if (closeLinkElement != null && closeLinkElement.isDisplayed(false)) {
		closeLinkElement.click(false);
	} else {
		this.linkElement.sendKeys(false/*recovery*/, Keys.ESCAPE);
	}
}

@Override
protected String getCloseButton(final boolean validate) {
	return "//a[@dojoattachpoint='_closeButton']";
}

/**
 * Return the xpath for the the title link element.
 *
 * @return The xpath as a {@link String}.
 */
@Override
protected String getTitleLinkXpath() {
	return ".//a[@dojoattachpoint='titleLink']";
}
}

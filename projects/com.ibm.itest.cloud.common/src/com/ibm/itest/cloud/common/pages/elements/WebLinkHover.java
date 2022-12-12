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
package com.ibm.itest.cloud.common.pages.elements;

import static com.ibm.itest.cloud.common.scenario.ScenarioUtils.*;
import static com.ibm.itest.cloud.common.utils.StringComparisonCriterion.EQUALS;

import org.openqa.selenium.By;

import com.ibm.itest.cloud.common.pages.WebPage;
import com.ibm.itest.cloud.common.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.scenario.errors.WaitElementTimeoutError;
import com.ibm.itest.cloud.common.utils.StringComparisonCriterion;


/**
 * Abstract class for any window opened as a simple link hover in a browser page.
 * <p>
 * Following functionalities are specialized by the simple hover:
 * <ul>
 * <li>{@link #check()}: Check the rich hover.</li>
 * <li>{@link #clickOnTitle()}: Click on the rich hover title link.</li>
 * <li>{@link #getTitle()}: Return the title of the hover.</li>
 * <li>{@link #getTitleElement()}: Return the hover title web element.</li>
 * <li>{@link #getTitleLinkXpath()}: Return the xpath for the the title link element.</li>
 * <li>{@link #open(WebBrowserElement)}: Open the rich hover by flying over
 * the given web element.</li>
 * </ul>
* </p><p>
 * Following operations are also defined or specialized for simple hovers:
 * <ul>
 * <li>{@link #closeAction(boolean)}: The action to perform to close the window.</li>
 * <li>{@link #getCloseButton(boolean)}: The button to close the hover.</li>
 * <li>{@link #getTitlePageClass()}: Return the class of the page opened when
 * clicking on the title.</li>
 * <li>{@link #waitForBrowserUrl()}: Wait for the browser URL to be stable.</li>
 * </ul>
  * </p>
 */
abstract public class WebLinkHover<P extends WebPage> extends WebTextHover {

	// Title
	String title;
	// Workaround
	protected boolean workaround = true;

public WebLinkHover(final WebPage page) {
	super(page);
}

/**
 * Check the rich hover.
 * <p>
 * Default check is to check the title. Subclass might add some additional
 * verification.
 * </p>
 * @throws ScenarioFailedError If the hover title fails the check.
 */
public void check() throws ScenarioFailedError {

	// Check first whether the hover is still displayed
	if (!this.element.isDisplayed()) {
		open(this.linkElement);
	}

	// Check the title
	checkTitle();
}

private void checkTitle() {

	// Get titles
	String richHoverText = getTitle();
	String linkText = this.linkElement.getText();
	linkText = linkText.replaceAll("\u200f", "");
	linkText = linkText.replaceAll("\u202b", "");
	linkText = linkText.replaceAll("\u202c", "");

	// Check title
	StringComparisonCriterion titleCriterion = getTitleComparisonCriterion();
	boolean valid = titleCriterion.compare(richHoverText, linkText);

	// Try to get rid of the number in the form: "number: <some text>"
	if (!valid) {
		if (richHoverText.matches("^\\d+:\\s.*")){
			int richHoverIndex = richHoverText.indexOf(":");
			richHoverText = richHoverText.substring(richHoverIndex + 2);
		}

		if (linkText.matches("^\\d+:\\s.*")){
			int linkIndex = linkText.indexOf(":");
			linkText = linkText.substring(linkIndex + 2);
		}

		valid = titleCriterion.compare(richHoverText, linkText);
	}

	// Try to get rid of the number in the form: "<some text> (number)"
	if (!valid) {
		if (richHoverText.matches(".*\\(\\d+\\)$")){
			int richHoverIndex = richHoverText.lastIndexOf("(");
			richHoverText = richHoverText.substring(0, richHoverIndex - 1);
		}

		if (linkText.matches(".*\\(\\d+\\)$")){
			int linkIndex = richHoverText.lastIndexOf('(');
			linkText = linkText.substring(0, linkIndex - 1);
		}

		valid = richHoverText.equals(linkText);
	}

	// Fails if nothing matches
	if (!valid) {
		throw new ScenarioFailedError(richHoverText+ " is not a valid rich hover title, expected title that" + titleCriterion + " '" + linkText+"'");
	}
}

/**
 * Click on the rich hover title link.
 * <p>
 * This actions leads to jump to the page pointed by the link we're hovering over.
 * </p>
 * @return The opened page after the click as a subclass of {@link WebPage}
 */
public P clickOnTitle() {
	if (DEBUG) debugPrintln("		+ Click on hover title to open "+getTitlePageClass().getName());

	// Reset frame as title and close button element are in default frame
	resetFrame();

	// Reopen if closed
	if (!this.element.isDisplayed(false)) {
		// Workaround
		debugPrintln("WORKAROUND: For some reason the hover was no longer opened, try to open it again...");
		open(this.linkElement);
	}

	// Get title element
	WebBrowserElement titleElement = getTitleElement();
	String titleText = titleElement.getText();
	if (DEBUG) debugPrintln("		  -> hover title is '"+titleText+"'");

//	// Click on title element
//	titleElement.click();
//
//	// Compute page url
//	String pageUrl = waitForBrowserUrl();

	// Open web page
	try {
		P newPage = getPage().openPageUsingLink(titleElement, getTitlePageClass(), titleText);
		// If hovers are opened with javascript, they sometimes persist when new page is opened
		if (this.browser.isInternetExplorer() && this.element.isDisplayed(false /* recovery */)) {
			close();
		}
		return newPage;
	}
	catch (WaitElementTimeoutError wete) {
		// Workaround: For some reason the page didn't load, retry
		if (this.workaround) {
			this.workaround = false;
			debugPrintln("WORKAROUND: For some reason the page didn't load after having clicked on rich hover title, retry...");
			debugPrintException(wete);
			return clickOnTitle();
		}
		throw wete;
	}
}

///**
// * The action to perform to close the window.
// * <p>
// * There's no close button for this dialog, the only way to close it is to click
// * somewhere else in the page.
// * </p><p>
// * First attempt to do this is to scroll to the top.
// * Second attempt to do hit the Escape key on the link element
// * </p>
// */
//@Override
//protected void closeAction(final boolean cancel) {
////	this.browser.scrollPageTop();
////	System.out.println("Need to find a way to close this hover!");
//	this.linkElement.sendKeys(Keys.ESCAPE);
//}
//
///**
// * {@inheritDoc}
// * <p>
// * There's no button close this kind of rich hover.
// * </p>
// */
//@Override
//protected String getCloseButton(final boolean validate) {
//	return null;
//}

/**
 * Return the title of the hover.
 *
 * @return The title as a {@link String}.
 */
public String getTitle() {
	if (this.title == null) {
		// Title is stored while getting the title element
		getTitleElement();
	}
	return this.title;
}

/**
 * Return the comparison criterion that should be used to compare the title of this hover with the text of its link element
 *
 * @return The {@link StringComparisonCriterion} that should be used
 */
protected StringComparisonCriterion getTitleComparisonCriterion() {
	return EQUALS;
}

/**
 * Return the hover title web element.
 *
 * @return The hover title element as {@link WebBrowserElement}
 * @throws ScenarioFailedError If the title element is not found
 */
public WebBrowserElement getTitleElement() {
	// Get the title element
	WebBrowserElement hoverTitleElement = this.element.waitForElement(By.xpath(getTitleLinkXpath()), timeout());
	if (hoverTitleElement == null) {
		// Check if there's a login
		WebBrowserElement loginElement = this.element.waitForElement(By.xpath(".//a[text()='Log in']"), 1/*sec*/);
		if (loginElement != null) {
			// Login
			// TODO Need improvement for distributed topology
			loginElement.click();
			// Wait again for the title
			hoverTitleElement = this.element.waitForElement(By.xpath(getTitleLinkXpath()), openTimeout());
		}
		if (hoverTitleElement == null) {
			// Workaround Select frame again
			selectFrame();
			// Wait again for the title
			hoverTitleElement = this.element.waitForElement(By.xpath(getTitleLinkXpath()), openTimeout());
			if (hoverTitleElement == null) {
				throw new WaitElementTimeoutError("The rich hover title was not found.");
			}
		}
	}

	// Store title
	this.title = hoverTitleElement.getText();

	// Check that title is not empty
	int n = 0;
	while (this.title.isEmpty()) {
		if (n++ > 5) {
			throw new WaitElementTimeoutError("The hover title is still empty after having tried again twice, hence give up...");
		}
		debugPrint("Workaround #"+n+": ");
		debugPrintln("	- wait "+n+" seconds...");
		sleep(n);

		// Check first whether the hover is still displayed
		if (this.element.isDisplayed()) {
			debugPrintln("The hover title is empty but hover is still displayed, just get the title element again to see if it's a transient issue...");
		} else {
			debugPrintln("The hover has vanished, try to reopen it again to see if it's a transient issue...");
			open(this.linkElement);
		}

		// Wait again for the title
		hoverTitleElement = this.element.waitForElement(By.xpath(getTitleLinkXpath()), openTimeout());
		this.title = hoverTitleElement.getText();
	}

	// Return the found title element
	return hoverTitleElement;
}

/**
 * Return the xpath for the the title link element.
 *
 * @return The xpath as a {@link String}.
 */
protected String getTitleLinkXpath() {
	return ".//a";
}

/**
 * Return the class of the page opened when clicking on the title.
 *
 * @return The class of the opened page as <P>
 */
abstract protected Class<P> getTitlePageClass();

/**
 * Wait for the browser URL to be stable.
 * <p>
 * Default is just wait one second, subclasses might add more efficient mechanism
 * typically by waiting for some specific string inside the URL.
 * </p>
 * @return The browser URL as a {@link String}.
 */
protected String waitForBrowserUrl() {
//	this.browser.waitInitialPageLoading(openTimeout());
	String pageUrl = this.browser.getCurrentUrl();
	if (DEBUG) debugPrintln("		  -> browser URL: '"+pageUrl+"'");
	return pageUrl;
}
}

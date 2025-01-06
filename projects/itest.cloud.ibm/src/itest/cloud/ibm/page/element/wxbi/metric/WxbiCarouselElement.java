/*********************************************************************
 * Copyright (c) 2018, 2022 IBM Corporation and others.
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
package itest.cloud.ibm.page.element.wxbi.metric;

import static itest.cloud.ibm.page.element.wxbi.metric.WxbiKeyMetricElement.*;
import static itest.cloud.scenario.ScenarioUtil.debugPrintln;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.element.IbmExpandableElement;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.ScenarioFailedError;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * This class defines and manages the <b>Carousel</b> expandable element.
 * <p>
 * The expansion and collapse are done using the avatar icon on page top right corner.
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #getKeyMetricElement(String, boolean)}: Return a given key metric element.</li>
 * <li>{@link #maximize()}: Maximize the key metric menu.</li>
 * <li>{@link #restore()}: Restore the key metric menu to its default size.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #isExpandable()}: Returns whether the current wrapped web element is expandable or not.</li>
 * <li>{@link #isExpanded()}: Returns whether the current wrapped web element is expanded or not.</li>
 * <li>{@link #getExpandableAttribute()}: Return the expandable attribute.</li>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * </ul>
 * </p>
 */
public class WxbiCarouselElement extends IbmExpandableElement {

public WxbiCarouselElement(final Page page) {
	super(page, page.waitForElement(By.xpath("//*[contains(@class,'resizable-bar-container')]"), page.timeout(), true /*fail*/, false /*displayed*/),
		By.xpath("//button[contains(@class,'header_visibility-button')]"));
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

/**
 * Return a given key metric element.
 *
 * @param name The name of the key metric as {@link String}.
 * @param fail Specifies whether to fail if a matching key metric element can not be found.
 *
 * @return The given key metric element as {@link WxbiKeyMetricElement} or
 * <code>null</code> if a matching key metric element can not be found and specified not to fail in such a situation.
 */
public WxbiKeyMetricElement getKeyMetricElement(final String name, final boolean fail) {
	final List<BrowserElement> keyMetricWebElements = waitForElements(
		By.xpath(KEY_METRIC_ELEMENT_LOCATOR_STRING + "[" + KEY_METRIC_ELEMENT_TITLE_LOCATOR_STRING + "[text()='" + name + "']]"),
		fail ? timeout() : tinyTimeout(), fail);
	return !keyMetricWebElements.isEmpty() ? new WxbiKeyMetricElement(this, keyMetricWebElements.get(keyMetricWebElements.size() - 1), name) : null;
}

private List<WxbiKeyMetricElement> getKeyMetricElements() {
	List<BrowserElement> keyMetricWebElements = waitForElements(KEY_METRIC_ELEMENT_LOCATOR);
	List<WxbiKeyMetricElement> keyMetricElements = new ArrayList<WxbiKeyMetricElement>(keyMetricWebElements.size());

	for (BrowserElement keyMetricWebElement : keyMetricWebElements) {
		final WxbiKeyMetricElement keyMetricElement = new WxbiKeyMetricElement(this, keyMetricWebElement);
		// Wait for the key metric element to load.
		keyMetricElement.waitForLoadingEnd();
		keyMetricElements.add(keyMetricElement);
	}

	return keyMetricElements;
}

@Override
protected By getTitleElementLocator() {
	return null;
}

@Override
public boolean isExpandable() throws ScenarioFailedError {
	return true;
}

@Override
public boolean isExpanded() throws ScenarioFailedError {
	return !this.element.getClassAttribute().contains("hidden");
}

private boolean isMaximized() {
	return this.element.getClassAttribute().contains("maximized");
}

/**
 * Maximize the key metric menu.
 */
public void maximize() {
	debugPrintln("		+ Maximize expandable web element " + getElementInfo());
	toggleSize(true /*maximize*/);
}

/**
 * Restore the key metric menu to its default size.
 */
public void restore() {
	debugPrintln("		+ Restore expandable web element " + getElementInfo());
	toggleSize(false /*maximize*/);
}

private void toggleSize(final boolean maximize) {
	if(isMaximized() == maximize) {
		return;
	}

	// Perform the action to bring the element to  the desired size.
	final BrowserElement toggleElement = this.page.waitForElement(By.xpath("//*[(text()='Maximize View') or (text()='Minimize View')]/../..//button"));
	toggleElement.click();

	// Check and raise an error if the resizing is not in the desired state.
	long timeoutMillis = timeout() * 1000 + System.currentTimeMillis();
	while (isMaximized() != maximize) {
		if (System.currentTimeMillis() > timeoutMillis) {
			StringBuilder messageBuilder = new StringBuilder("Could not ")
				.append(maximize ? "maximize" : "restore")
				.append(" web element: ")
				.append(getElementInfo())
				.append(" using expand element: ").append(toggleElement);
			throw new WaitElementTimeoutError(messageBuilder.toString());
		}
	}
}

@Override
public void waitForLoadingEnd() {
	super.waitForLoadingEnd();
	// Wait for each key metric element to load.
	getKeyMetricElements();
}
}
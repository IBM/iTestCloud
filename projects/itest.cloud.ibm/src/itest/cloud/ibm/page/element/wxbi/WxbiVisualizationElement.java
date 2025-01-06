/*********************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
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
package itest.cloud.ibm.page.element.wxbi;

import static itest.cloud.ibm.entity.wxbi.conversation.WxbiVisualizationType.getType;
import static itest.cloud.scenario.ScenarioUtil.println;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.entity.wxbi.conversation.WxbiVisualizationType;
import itest.cloud.ibm.page.dialog.IbmConfirmationDialog;
import itest.cloud.ibm.page.dialog.wxbi.WxbiKeyMetricDialog;
import itest.cloud.ibm.page.element.IbmDynamicDropdownlistElement;
import itest.cloud.ibm.page.element.IbmElementWrapper;
import itest.cloud.ibm.page.element.wxbi.conversation.WxbiConversationEditorElement;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * This class defines and manages a visualization element in {@link WxbiKeyMetricDialog} or {@link WxbiConversationEditorElement}.
 * <p>
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #getLiveWidgetElement()}: Return the live widget element associated with the visualization element.</li>
 * <li>{@link #getName()}: Return the live widget element associated with the visualization element.</li>
 * <li>{@link #getSupportedVisualizationTypes()}: Return the supported types of visualization by this element.</li>
 * <li>{@link #getVisualizationType()}: Return the visualization type of the live widget element.</li>
 * <li>{@link #openVisualization(WxbiVisualizationType)}: Open a given type of visualization.</li>
 * <li>{@link #pin()}: Pin the visualization in the carousel.</li>
 * <li>{@link #unpin()}: Unpin the visualization from the carousel.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #getTitleElement()}: Return the title element.</li>
 * <li>{@link #waitForLoadingEnd()}: Wait for loading of the wrapped element to complete.</li>
 * </ul>
 * </p>
 */
public class WxbiVisualizationElement extends IbmElementWrapper {

	private static final String ATTRIBUTE_CHART_TYPE = "chart-type";
	public static final By VISUALIZATION_ELEMENT_LOCATOR =
		By.xpath(".//*[(@class='ripasso-visualization-with-types') or (@class='ripasso-visualization-with-tabs')]");

public WxbiVisualizationElement(final ElementWrapper parent, final BrowserElement element) {
	super(parent, element);
	this.data = new String[] {getTitleElement().getText()};
}

public WxbiVisualizationElement(final ElementWrapper parent, final String... data) {
	super(parent, parent.waitForElement(VISUALIZATION_ELEMENT_LOCATOR), data);
}

@Override
protected Pattern getExpectedTitle() {
	final String name = getName();
	return (name != null) ? compile(quote(getName())) : null;
}

/**
 * Return the live widget element associated with the visualization element.
 *
 * @return The live widget element associated with the visualization element as {@link WxbiLiveWidgetElement}.
 */
public WxbiLiveWidgetElement getLiveWidgetElement() {
	WxbiLiveWidgetElement liveWidgetElement = new WxbiLiveWidgetElement(this);
	// Wait for the content of the visualization element to load.
	liveWidgetElement.waitForLoadingEnd();

	return liveWidgetElement;
}

/**
 * Return the name of the visualization.
 *
 * @return The name of the visualization as {@link String}.
 */
public String getName() {
	return (this.data != null) && (this.data.length > 0) ? this.data[0] : null;
}

/**
 * Return the supported types of visualization by this element.
 *
 * @return The supported types of visualization by this element as {@link List} of {@link String}.
 */
public List<String> getSupportedVisualizationTypes() {
	final IbmDynamicDropdownlistElement dropdownlistElement = getVisualizationTypeDropdownListElement();
	return dropdownlistElement.getOptions();
}

@Override
protected BrowserElement getTitleElement() {
	return waitForElement(getTitleElementLocator(), timeout(), true /*fail*/, false /*displayed*/);
}

@Override
protected By getTitleElementLocator() {
	return By.xpath(".//h6");
}

private BrowserElement getToolbarButtonElement(final String label) {
	return waitForElement(By.xpath(".//*[contains(@class,'header-toolbar')]/*[.//*[contains(@class,'tooltip-content') and text()='" + label + "']]"));
}

/**
 * Return the visualization type of the live widget element.
 *
 * @return The visualization type of the live widget element as {@link WxbiVisualizationType}.
 */
public WxbiVisualizationType getVisualizationType() {
	final BrowserElement liveWidgetContentElement = waitForElement(
		By.xpath(".//*[contains(@class,'ripasso-visualization') and @" + ATTRIBUTE_CHART_TYPE + " != ''] | .//*[contains(@class,'liveWidgetContent')]"),
		timeout(), true /*fail*/, false /*displayed*/);
	final String walkmeDataId = liveWidgetContentElement.getAttribute("walkme-data-id");

	return getType((walkmeDataId != null) ? walkmeDataId : liveWidgetContentElement.getAttribute(ATTRIBUTE_CHART_TYPE));
}

private IbmDynamicDropdownlistElement getVisualizationTypeDropdownListElement() {
	return new IbmDynamicDropdownlistElement(
		getPage(), By.xpath("//*[starts-with(@id,'overflowmenu')]"),
		getToolbarButtonElement("Options" /*label*/), null /*selectionLocator*/, By.xpath(".//*[contains(@class,'label')]"));
}

/**
 * Open a given type of visualization.
 *
 * @param type The type of the visualization to open as {@link WxbiVisualizationType}.
 *
 * @return The opened live widget element representing the desired visualization type as {@link WxbiLiveWidgetElement}.
 */
public WxbiLiveWidgetElement openVisualization(final WxbiVisualizationType type) {
	WxbiVisualizationType currentType = getVisualizationType();

	if(currentType.equals(type)) {
		println("	  -> A visualization of desired type '" + type.label + "' is already open and therefore, reused for this scenario.");
		return getLiveWidgetElement();
	}

	final IbmDynamicDropdownlistElement dropdownlistElement = getVisualizationTypeDropdownListElement();
	dropdownlistElement.select(type.label);
	// Wait for the new visualization to load.
	waitForLoadingEnd();

	// Wait for the desired visualization to open and load its contents.
	long timeoutMillis = timeout() * 1000 + System.currentTimeMillis();
	while (!(currentType = getVisualizationType()).equals(type)) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("A visualization of the desired type '" + type.label + "' had not opened before the timeout '" + timeout() + "'s had reached.");
		}
	}
	return getLiveWidgetElement();
}

/**
 * Pin the visualization in the carousel.
 */
public void pin() {
	getToolbarButtonElement("Pin" /*label*/).click();
}

/**
 * Unpin the visualization from the carousel.
 */
public void unpin() {
	final IbmConfirmationDialog confirmationDialog = new IbmConfirmationDialog(getPage()) {
		@Override
		protected Pattern getExpectedTitle() {
			return compile("Unpin .+");
		}

		@Override
		protected String getPrimaryButtonText() {
			return "Yes, unpin from carousel";
		}
	};
	confirmationDialog.open(getToolbarButtonElement("Unpin" /*label*/));
	confirmationDialog.close();
}

@Override
public void waitForLoadingEnd() {
	super.waitForLoadingEnd();
	// Wait for the content of the visualization element to load.
	getLiveWidgetElement();
}
}
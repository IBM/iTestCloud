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
package itest.cloud.ibm.page.wxbia.modeling;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.config.User;
import itest.cloud.ibm.config.IbmConfig;
import itest.cloud.ibm.page.dialog.wxbi.modeling.WxbiCreateMetricDefinitionDialog;
import itest.cloud.ibm.page.element.IbmTabListElement;
import itest.cloud.ibm.page.element.wxbi.metric.WxbiCarouselElement;
import itest.cloud.ibm.page.element.wxbi.modeling.*;
import itest.cloud.ibm.page.wxbia.WxbiPage;
import itest.cloud.ibm.scenario.IbmScenarioUtil;

/**
 * This class represents and manages the <b>Modeling</b> page of the WatsonX BI Assistant application.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #createMetricDefinition(String)}: Create a metric definition.</li>
 * <li>{@link #getSemanticModelMenuElement()}: Return the Semantic Model element.</li>
 * <li>{@link #openGridTab()}: Open the Grid tab.</li>
 * <li>{@link #selectItemInSemanticModel(String)}: Select a given item in the semantic model.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getBusyIndicatorElement()}: Return the element indicating that the page is undergoing an operation (busy).</li>
 * <li>{@link #getExpectedTitle()}: Return the expected title for the current web page.</li>
 * <li>{@link #getTitleElementLocator()}: Return the title element locator.</li>
 * <li>{@link #waitForLoadingPageEnd()}: Wait for the page loading to be finished.</li>
 * </ul>
 * </p>
 */
public class WxbiModelingPage extends WxbiPage {

public WxbiModelingPage(final String url, final IbmConfig config, final User user) {
	super(url, config, user);
}

/**
 * Create a metric definition.
 *
 * @return The opened create metric definition dialog as {@link WxbiCreateMetricDefinitionDialog}.
 */
public WxbiCreateMetricDefinitionDialog createMetricDefinition(final String modelItem) {
	final WxbiSemanticModelMenuElement semanticModelMenuElement = getSemanticModelMenuElement();
	final WxbiSemanticModelElement semanticModelElement = semanticModelMenuElement.getSemanticModelElement(modelItem);
	return semanticModelElement.createMetricDefinition();
}

@Override
protected By getBusyIndicatorElementLocator() {
	return IbmScenarioUtil.getBusyIndicatorElementLocator(false /*relative*/, "//*[contains(text(),'Fetching semantic model')]");
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

/**
 * Return the <b>Semantic Model</b> menu element.
 *
 * @return The Semantic Model menu element as {@link WxbiCarouselElement}.
 */
public WxbiSemanticModelMenuElement getSemanticModelMenuElement() {
	WxbiSemanticModelMenuElement semanticModelMenuElement = new WxbiSemanticModelMenuElement(this);
	// Wait for the semantic model menu element to load.
	semanticModelMenuElement.waitForLoadingEnd();

	return semanticModelMenuElement;
}

@Override
protected By getTitleElementLocator() {
	return null;
}

/**
 * Open the <b>Grid</b> tab.
 *
 * @return The opened Grid tab as {@link WxbiGridTabElement}.
 */
public WxbiGridTabElement openGridTab() {
	return openTab("Grid", WxbiGridTabElement.class);
}

private <T extends WxbiModelingPageTabElement> T openTab(final String name, final Class<T> tabClass) {
	IbmTabListElement tablistElement = new IbmTabListElement(this);

	return tablistElement.openTab(name, tabClass);
}

/**
 * Select a given item in the semantic model.
 *
 * @param name The name of the item in the semantic model to select as {@link String}.
 */
public void selectItemInSemanticModel(final String name) {
	final WxbiSemanticModelMenuElement semanticModelMenuElement = getSemanticModelMenuElement();
	final WxbiSemanticModelElement semanticModelElement = semanticModelMenuElement.getSemanticModelElement(name);
	semanticModelElement.select();
}

@Override
public void waitForLoadingPageEnd() {
	super.waitForLoadingPageEnd();
	// Wait for the semantic model menu element to load.
	getSemanticModelMenuElement();
}
}
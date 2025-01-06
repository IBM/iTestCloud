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
package itest.cloud.ibm.page.element.wxbi.modeling;

import static itest.cloud.scenario.ScenarioUtil.println;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.dialog.wxbi.modeling.WxbiCreateMetricDefinitionDialog;
import itest.cloud.ibm.page.element.IbmElementWrapper;
import itest.cloud.ibm.page.element.wxbi.WxbiContextMenuElement;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;

/**
 * This class defines and manages an item in the semantic model in {@link WxbiSemanticModelMenuElement}.
 * <p>
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #createMetricDefinition()}: Create a metric definition.</li>
 * <li>{@link #getName()}: Return the name of the semantic model element.</li>
 * <li>{@link #isSelected()}: Specify whether this conversation element is selected.</li>
 * <li>{@link #select()}: Select the semantic model element.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * </ul>
 * </p>
 */
public class WxbiSemanticModelElement extends IbmElementWrapper {

public WxbiSemanticModelElement(final ElementWrapper parent, final BrowserElement element, final String... data) {
	super(parent, element, data);
}

/**
 * Create a metric definition.
 *
 * @return The opened create metric definition dialog as {@link WxbiCreateMetricDefinitionDialog}.
 */
public WxbiCreateMetricDefinitionDialog createMetricDefinition() {
	final WxbiContextMenuElement contextMenuElement = getContextMenuElement();
	final WxbiCreateMetricDefinitionDialog createMetricDefinitionDialog =
		contextMenuElement.selectByOpeningDialog("Create metric definition", WxbiCreateMetricDefinitionDialog.class, getName());
	return createMetricDefinitionDialog;
}

private WxbiContextMenuElement getContextMenuElement() {
	return new WxbiContextMenuElement(getPage(),
		By.xpath("//*[contains(@class,'context-menu')]"), getElement(), null /*selectionLocator*/,
		By.xpath(".//*[contains(@class,'truncatedText__text')]"));
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

/**
 * Return the name of the semantic model element.
 *
 * @return The name of the semantic model element as {@link String}.
 */
public String getName() {
	return this.data[0];
}

@Override
protected By getTitleElementLocator() {
	return null;
}

/**
 * Specify whether this semantic model element is selected.
 *
 * @return <code>true</code> if this semantic model element is selected or <code>false</code> otherwise.
 */
public boolean isSelected() {
	return this.element.getAriaSelectedAttribute();
}

/**
 * Select the semantic model element.
 */
public void select() {
	if(isSelected()) {
		println("	  -> A item with the name '" + getName() + "' was already selected in the semantic model and therefore, no attempt was made to select it.");
		return;
	}

	this.element.click();
	// Wait for the corresponding table to load if applicable.
	getPage().waitForLoadingPageEnd();
}
}
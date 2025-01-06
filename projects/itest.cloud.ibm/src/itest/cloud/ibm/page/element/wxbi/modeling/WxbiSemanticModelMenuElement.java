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

import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.element.IbmElementWrapper;
import itest.cloud.ibm.page.wxbia.modeling.WxbiModelingPage;
import itest.cloud.page.Page;

/**
 * This class represents the <b>Semantic Model</b> menu element in {@link WxbiModelingPage} and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #getSemanticModelElement(String)}: Return a given semantic model element.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * <li>{@link #waitForLoadingEnd()}: Wait for loading of the wrapped element to complete.</li>
 * </ul>
 * </p>
 */
public class WxbiSemanticModelMenuElement extends IbmElementWrapper {

public WxbiSemanticModelMenuElement(final Page page) {
	super(page, By.xpath("//*[@data-tid='modelTreePane']"));
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote("Semantic model"));
}

private void getSementicModelElement() {
	waitForElement(By.xpath(".//*[@data-tid='ba-tree__list']"));
}

@Override
protected By getTitleElementLocator() {
	return By.xpath(".//*[contains(@class,'Header-title')]/*");
}

/**
 * Return a given semantic model element.
 *
 * @param name The name of the semantic model element.
 *
 * @return The given semantic model element as {@link WxbiSemanticModelElement}.
 */
public WxbiSemanticModelElement getSemanticModelElement(final String name) {
	return new WxbiSemanticModelElement(this, waitForElement(
		By.xpath(".//*[(@role='treeitem')]//*[contains(@data-tid,'text-node') and (text()='" + name + "')]")), name);
}

@Override
public void waitForLoadingEnd() {
	super.waitForLoadingEnd();
	// Wait for the semantic model to load.
	getSementicModelElement();
}
}
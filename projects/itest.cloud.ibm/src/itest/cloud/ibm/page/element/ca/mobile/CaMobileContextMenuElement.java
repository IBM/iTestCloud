/*********************************************************************
 * Copyright (c) 2024, 2025 IBM Corporation and others.
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
package itest.cloud.ibm.page.element.ca.mobile;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.element.IbmDynamicDropdownlistElement;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;

/**
 * This class to represents a generic context menu element in the Cognos Analytics Mobile application where the element is only made available after clicking on the expansion element.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getOptionElementLabel(BrowserElement)}: Return the label associated with a given option element.</li>
 * <li>{@link #selectOptionElement(BrowserElement)}: Select a given option element.</li>
 * </ul>
 * </p>
 */
public class CaMobileContextMenuElement extends IbmDynamicDropdownlistElement {

public CaMobileContextMenuElement(final Page page, final BrowserElement expansionElement) {
	super(page, By.xpath("//*[contains(name(),'ScrollView')]/*[contains(name(),'ViewGroup') and ./*[contains(name(),'Button')]]"), expansionElement, null /*selectionLocator*/, By.xpath(".//*[contains(@class,'TextView')]"));
}

@Override
protected String getOptionElementLabel(final BrowserElement optionItemElement) {
	return optionItemElement.getTextAttribute();
}

@Override
protected void selectOptionElement(final BrowserElement optionElement) {
	// The default selection operation is performed by clicking the provided option element via JavaScript,
	// which causes a UnsupportedCommandException on a Mobile Emulator. Therefore, perform the
	// selection operation by placing a regular click on the provided option element.
	optionElement.click();
}
}
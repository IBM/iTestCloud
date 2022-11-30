/*********************************************************************
 * Copyright (c) 2016, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.common.tests.web;

import org.openqa.selenium.By;

/**
 * Manage a menu used from a CKE Editor.
 *<p>
 * Following methods are overridden on this menu:
 * <ul>
 * <li>{@link #getItemElementsLocator()}: Returns the locator of the menu item elements.</li>
 * <li>{@link #getItemXpath(String)}: Returns the locator for the given item.</li>
 * </ul>
 * </p>
 */
public class CKEditorMenu extends WebMenu {

public CKEditorMenu(final WebPage page) {
	super(page, By.xpath("//iframe[contains(@class,'cke_panel_frame')]"), false, true);
}

@Override
protected By getItemElementsLocator() {
	return By.xpath("//a[contains(@class,'cke_menubutton')]");
}

@Override
protected String getItemXpath(final String itemLabel) {
	return "//a[.//span[@class='cke_menubutton_label' and text()='"+itemLabel+"']]";
}
}

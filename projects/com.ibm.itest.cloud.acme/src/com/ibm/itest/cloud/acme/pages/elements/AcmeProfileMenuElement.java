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
package com.ibm.itest.cloud.acme.pages.elements;

import org.openqa.selenium.By;

import com.ibm.itest.cloud.common.pages.WebPage;

/**
 * This class defines and manages the <b>Profile</b> menu element.
 * <p>
 * The expansion and collapse are done using the avatar icon on page top right corner.
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #getExpandableAttribute()}: Return the expandable attribute.</li>
 * </ul>
 * </p>
 */
public class AcmeProfileMenuElement extends AcmeMenuElement {

/**
 * Create an instance belonging to the given web page.
 *
 * @param page The page in which the created instance will belong to
 */
public AcmeProfileMenuElement(final WebPage page) {
	super(page, By.id("dap-profile"), By.xpath(".//*[contains(@class,'toggle')]"));
}
}
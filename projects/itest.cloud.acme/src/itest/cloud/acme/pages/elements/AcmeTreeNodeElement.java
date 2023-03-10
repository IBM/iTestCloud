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
package itest.cloud.acme.pages.elements;

import org.openqa.selenium.By;

import itest.cloud.acme.pages.AcmeAbstractWebPage;
import itest.cloud.pages.Page;
import itest.cloud.pages.elements.BrowserElement;
import itest.cloud.pages.elements.TreeNodeElement;

/**
 * This class represents a node or a leaf element of a tree element and manages its common
 * functionality.
 * <p>
 * The following is an example for such a tree node element</br>
 * <xmp>
 * <li class="TreeNode">
 *     <div class="LabelWrapper">
 *         <a class="LabelLink">Notebooks</a>
 *         <span class="ExpansionIcon"/>
 *     </div>
 *     <ul class="TreeChildrenContainer">
 *         <li class="TreeChild">
 *             <div class="LabelWrapper">
 *                 <a class="LabelLink">Create notebook</a>
 *                 <span class="ExpansionIcon"/>
 *                 <ul class="TreeChildrenContainer">...child tree nodes go here...</ul>
 *             </div>
 *         </li>
 *         <li class="TreeChild">
 *             <div class="LabelWrapper">
 *                 <a class="LabelLink">Updates</a>
 *                 <span class="ExpansionIcon"/>
 *                 <ul class="TreeChildrenContainer">...child tree nodes go here...</ul>
 *             </div>
 *         </li>
 *     </ul>
 * </li>
 * </xmp>
 * In the above example, the locator, labelBy, expansionBy, and childrenBy parameters of
 * the constructor shall be defined as follows:</br>
 * <ul>
 * <li>locator: //li[@class='TreeNode']</li>
 * <li>labelBy: ./div/a[@class='LabelLink']</li>
 * <li>expansionBy: ./div/span[@class='ExpansionIcon']</li>
 * <li>childrenBy: ./ul[@class='TreeChildrenContainer']/li[@class='TreeChild']</li>
 * </ul>
 * </p>
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getPage()}: Return the web page from which the window has been opened.</li>
 * </ul>
 * </p>
 */
public class AcmeTreeNodeElement extends TreeNodeElement {

public AcmeTreeNodeElement(final Page page, final By locator, final By labelBy, final By expansionBy, final By childrenBy) {
	super(page, locator, labelBy, expansionBy, childrenBy);
}

public AcmeTreeNodeElement(final Page page, final BrowserElement webElement, final By labelBy, final By expansionBy, final By childrenBy) {
	super(page, webElement, labelBy, expansionBy, childrenBy);
}

@Override
protected AcmeAbstractWebPage getPage() {
	return (AcmeAbstractWebPage) super.getPage();
}
}

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

import com.ibm.itest.cloud.common.tests.scenario.errors.ScenarioFailedError;

/**
 * This class represents a tree element and manages its common functionality.
 * <p>
 * The tree element should not be a tree node element. Instead, a tree element
 * should contain one to many tree node elements and their child tree node elements
 * and so forth.
 * </p>
 * <p>
 * The following is an example for such a tree element</br>
 * <xmp>
 * <div class="Tree">
 *     <li class="TreeNode">
 *         <div class="LabelWrapper">
 *             <a class="LabelLink">Notebooks</a>
 *             <span class="ExpansionIcon"/>
 *         </div>
 *         <ul class="TreeChildrenContainer">
 *             <li class="TreeChild">
 *                 <div class="LabelWrapper">
 *                     <a class="LabelLink">Create notebook</a>
 *                     <span class="ExpansionIcon"/>
 *                     <ul class="TreeChildrenContainer">...child tree nodes go here...</ul>
 *                 </div>
 *             </li>
 *             <li class="TreeChild">
 *                 <div class="LabelWrapper">
 *                     <a class="LabelLink">Updates</a>
 *                     <span class="ExpansionIcon"/>
 *                     <ul class="TreeChildrenContainer">...child tree nodes go here...</ul>
 *                 </div>
 *             </li>
 *         </ul>
 *     </li>
 * </div>
 * </xmp>
 * In the above example, the locator, labelBy, expansionBy, and childrenBy parameters of
 * the constructor shall be defined as follows:</br>
 * <ul>
 * <li>locator: //div[@class='Tree']</li>
 * <li>labelBy: ./div/a[@class='LabelLink']</li>
 * <li>expansionBy: ./div/span[@class='ExpansionIcon']</li>
 * <li>childrenBy: ./ul[@class='TreeChildrenContainer']/li[@class='TreeChild']</li>
 * </ul>
 * </p>
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #collapse()}: Collapse the current web element.</li>
 * <li>{@link #expand()}: Expand the current web element.</li>
 * <li>{@link #isExpandable()}: Returns whether the current wrapped web element is expandable or not.</li>
 * <li>{@link #isExpanded()}: Returns whether the current wrapped web element is expanded or not.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * </ul>
 * </p>
 */
public class WebTreeElement extends WebTreeNodeElement {

/**
 * Create an instance of a tree element.
 *
 * @param page The web page containing the tree element and its tree node elements.
 * @param locator The xpath to locate the tree element as {@link By}.
 * @param labelBy The xpath to locate the label element of a tree node element of the
 * tree element as {@link By}.
 * This xpath must be relative to a tree node element.
 * @param expansionBy The xpath to locate the element used to expand or collapse a tree node
 * element of the tree element as {@link By}.This xpath must be relative to a tree node element.
 * @param childrenBy The xpath to locate the child tree node elements of a tree node element
 * of the tree element as {@link By}. This xpath must be relative to a tree node element.
 */
public WebTreeElement(final WebPage page, final By locator, final By labelBy, final By expansionBy, final By childrenBy) {
	super(page, locator, labelBy, expansionBy, childrenBy);
}

/**
 * Create an instance of a tree element.
 *
 * @param page The web page containing the tree element and its tree node elements.
 * @param webElement The tree element as {@link WebBrowserElement}.
 * @param labelBy The xpath to locate the label element of a tree node element of the
 * tree element as {@link By}.
 * This xpath must be relative to a tree node element.
 * @param expansionBy The xpath to locate the element used to expand or collapse a tree node
 * element of the tree element as {@link By}.This xpath must be relative to a tree node element.
 * @param childrenBy The xpath to locate the child tree node elements of a tree node element
 * of the tree element as {@link By}. This xpath must be relative to a tree node element.
 */
public WebTreeElement(final WebPage page, final WebBrowserElement webElement, final By labelBy, final By expansionBy, final By childrenBy) {
	super(page, webElement, labelBy, expansionBy, childrenBy);
}

@Override
public void collapse() throws ScenarioFailedError {
	// Do nothing.
}

@Override
public void expand() throws ScenarioFailedError {
	// Do nothing.
}

@Override
public boolean isExpandable() throws ScenarioFailedError {
	return true;
}

@Override
public boolean isExpanded() throws ScenarioFailedError {
	return true;
}
}

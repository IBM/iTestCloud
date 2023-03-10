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
package itest.cloud.pages.elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.pages.Page;
import itest.cloud.scenario.errors.ScenarioFailedError;
import itest.cloud.scenario.errors.WaitElementTimeoutError;

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
 * <li>{@link #collapseAll()}: Collapse the entire tree node including all its child tree nodes.</li>
 * <li>{@link #expandAll()}: Expand the entire tree node including all its child tree nodes.</li>
 * <li>{@link #getLabelElement()}: Return the label element of the tree node or tree leaf element.</li>
 * <li>{@link #isExpandable()}: Returns whether the current wrapped web element is expandable or not.</li>
 * <li>{@link #isExpanded()}: Returns whether the current wrapped web element is expanded or not.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #createChildElement(BrowserElement)}: Create a element wrapper for a given child tree node or tree leaf element.</li>
 * <li>{@link #getAllChildElements()}: Return the child tree node and tree leaf elements at all levels of the current tree node.</li>
 * <li>{@link #getChildElement(String, boolean)}: Return the child tree node or tree leaf element at a given path.</li>
 * <li>{@link #getChildElements()}: Return the child tree node and tree leaf elements of the current tree node.</li>
 * <li>{@link #getExpansionElement()}: Return the web element used to expand or collapse the tree node element.</li>
 * <li>{@link #getLabel()}: Return the label of the tree node or tree leaf element.</li>
 * <li>{@link #isChildElementAvailable(String)}: Specifies whether a child tree node or tree leaf element is available at a given path.</li>
 * <li>{@link #isChildElementAvailable(String, boolean)}: Specifies whether a child tree node or tree leaf element is available at a given path.</li>
 * </ul>
 * </p>
 */
public class TreeNodeElement extends ExpandableElement {

	public static final String PATH_SEPARATOR = "/";

	protected final By labelLocator;
	protected final By expansionLocator;
	protected final By childrenLocator;

/**
 * Create an instance of a tree node element.
 *
 * @param parent The web element containing the tree node element.
 * @param locator The xpath to locate the tree node element as {@link By}.
 * @param labelLocator The xpath to locate the label element of the tree node element as {@link By}.
 * This xpath must be relative to the tree node element.
 * @param expansionLocator The xpath to locate the element used to expand or collapse the tree node
 * element as {@link By}.This xpath must be relative to the tree node element.
 * @param childrenLocator The xpath to locate the child tree node elements of this tree node element
 * as {@link By}. This xpath must be relative to the tree node element.
 */
public TreeNodeElement(final ElementWrapper parent, final By locator, final By labelLocator, final By expansionLocator, final By childrenLocator) {
	super(parent, locator);
	this.labelLocator = labelLocator;
	this.expansionLocator = expansionLocator;
	this.childrenLocator = childrenLocator;
	this.expansionElement = getExpansionElement();
}

/**
 * Create an instance of a tree node element.
 *
 * @param parent The web element containing the tree node element.
 * @param webElement The tree node element as {@link BrowserElement}.
 * @param labelLocator The xpath to locate the label element of the tree node element as {@link By}.
 * This xpath must be relative to the tree node element.
 * @param expansionLocator The xpath to locate the element used to expand or collapse the tree node
 * element as {@link By}.This xpath must be relative to the tree node element.
 * @param childrenLocator The xpath to locate the child tree node elements of this tree node element
 * as {@link By}. This xpath must be relative to the tree node element.
 */
public TreeNodeElement(final ElementWrapper parent, final BrowserElement webElement, final By labelLocator, final By expansionLocator, final By childrenLocator) {
	super(parent, webElement);
	this.labelLocator = labelLocator;
	this.expansionLocator = expansionLocator;
	this.childrenLocator = childrenLocator;
	this.expansionElement = getExpansionElement();
}

/**
 * Create an instance of a tree node element.
 *
 * @param page The web page containing the tree node element.
 * @param locator The xpath to locate the tree node element as {@link By}.
 * @param labelLocator The xpath to locate the label element of the tree node element as {@link By}.
 * This xpath must be relative to the tree node element.
 * @param expansionLocator The xpath to locate the element used to expand or collapse the tree node
 * element as {@link By}.This xpath must be relative to the tree node element.
 * @param childrenLocator The xpath to locate the child tree node elements of this tree node element
 * as {@link By}. This xpath must be relative to the tree node element.
 */
public TreeNodeElement(final Page page, final By locator, final By labelLocator, final By expansionLocator, final By childrenLocator) {
	super(page, locator);
	this.labelLocator = labelLocator;
	this.expansionLocator = expansionLocator;
	this.childrenLocator = childrenLocator;
	this.expansionElement = getExpansionElement();
}

/**
 * Create an instance of a tree node element.
 *
 * @param page The web page containing the tree node element.
 * @param webElement The tree node element as {@link BrowserElement}.
 * @param labelLocator The xpath to locate the label element of the tree node element as {@link By}.
 * This xpath must be relative to the tree node element.
 * @param expansionLocator The xpath to locate the element used to expand or collapse the tree node
 * element as {@link By}.This xpath must be relative to the tree node element.
 * @param childrenLocator The xpath to locate the child tree node elements of this tree node element
 * as {@link By}. This xpath must be relative to the tree node element.
 */
public TreeNodeElement(final Page page, final BrowserElement webElement, final By labelLocator, final By expansionLocator, final By childrenLocator) {
	super(page, webElement);
	this.labelLocator = labelLocator;
	this.expansionLocator = expansionLocator;
	this.childrenLocator = childrenLocator;
	this.expansionElement = getExpansionElement();
}

/**
 * Collapse the entire tree node including all its child tree nodes.
 */
public void collapseAll() {
	if(isExpanded()) {
		for (TreeNodeElement childElement : getChildElements()) {
			childElement.collapseAll();
		}

		collapse();
	}
}

/**
 * Create a element wrapper for a given child tree node or tree leaf element.
 * <p>
 * This method shall be overridden by a subclass by creating its own instance as
 * the wrapper of the given child tree node or tree leaf element.
 * </p>
 *
 * @param childWebElement The child tree or tree leaf element as {@link BrowserElement}.
 *
 * @return The element wrapper for the given child tree node or tree leaf element as {@link TreeNodeElement}.
 */
protected TreeNodeElement createChildElement(final BrowserElement childWebElement) {
	return new TreeNodeElement(this.page, childWebElement, this.labelLocator, this.expansionLocator, this.childrenLocator);
}

/**
 * Expand the entire tree node including all its child tree nodes.
 */
public void expandAll() {
	if(isExpandable()) {
		expand();

		for (TreeNodeElement childElement : getChildElements()) {
			childElement.expandAll();
		}
	}
}

/**
 * Return the child tree node and tree leaf elements at all levels of the current tree node.
 *
 * @return The child tree node and tree leaf elements at all levels of the current tree node as {@link List}.
 */
protected List<TreeNodeElement> getAllChildElements() {
	if(isExpandable()) expand();

	List<TreeNodeElement> allChildElements = new ArrayList<TreeNodeElement>();
	List<TreeNodeElement> childElements = getChildElements();

	for (TreeNodeElement childElement : childElements) {
		allChildElements.add(childElement);
		allChildElements.addAll(childElement.getAllChildElements());
	}

	return allChildElements;
}

/**
 * Return the child tree node or tree leaf element at a given path.
 * <p>
 * If the path contains multiple sections, they must be separated by the character '/'.
 * The following are some example paths.
 * <ul>
 * <li>Quick overview</li>
 * <li>Visualizations/Model visualizations</li>
 * <li>Catalogs/Monitor data usage and user activity/Classification types</li>
 * </ul>
 * </p>
 *
 * @param path The path to the child tree node or tree leaf.
 * @param fail Specifies whether to fail if a matching child tree node or tree leaf
 * element is not found.
 * @return The child tree node or tree leaf element at the given path as {@link TreeNodeElement} or
 * <code>null</code> if a matching element is not found and asked not to fail.
 */
protected TreeNodeElement getChildElement(final String path, final boolean fail) {
	String[] pathElements = path.split(PATH_SEPARATOR);
	String expectedLabel = pathElements[0];
	if(isExpandable()) expand();
	// All child elements must be visible at this time since their parents element has been expanded. However, some node
	// elements may be added to the tree dynamically. Therefore, need to poll with a timeout if the desired child element
	// if not found in the first pass.
	int timeout = fail ? timeout() : tinyTimeout();
	long timeoutMillis = timeout * 1000 + System.currentTimeMillis();

	do {
		for (TreeNodeElement childElement : getChildElements()) {
			String actualLabel = childElement.getLabel();

			if(childElement.isDisplayed(false /*recovery*/) && (actualLabel != null) &&
			   (expectedLabel.equalsIgnoreCase(actualLabel) || Pattern.compile(expectedLabel, Pattern.DOTALL).matcher(actualLabel).matches())) {

				if(pathElements.length == 1) {
					return childElement;
				}

				return childElement.getChildElement(path.substring(expectedLabel.length() + 1), fail);
			}
		}
	} while (System.currentTimeMillis() <= timeoutMillis);

	// Return null if asked not to fail (i.e. fail=false) if a child element is not found.
	if(!fail) return null;
	// Throw an exception if asked to fail (i.e. fail=false) if a child element is not found.
	throw new WaitElementTimeoutError("An element with label '" + expectedLabel + "' could not be found before timeout '" + timeout + "' seconds");
}

/**
 * Return the child tree node and tree leaf elements of the current tree node.
 *
 * @return The child tree node and tree leaf elements of the current tree node as {@link List}.
 */
protected List<TreeNodeElement> getChildElements() {
	List<TreeNodeElement> childWebFolderElements = new ArrayList<TreeNodeElement>();
	List<BrowserElement> childWebElements = waitForElements(this.childrenLocator, tinyTimeout(), false /*fail*/, false /*displayed*/);

	for (BrowserElement childWebElement : childWebElements) {
		childWebFolderElements.add(createChildElement(childWebElement));
	}

	return childWebFolderElements;
}

@Override
protected String getExpandableAttribute() {
	throw new ScenarioFailedError("This method should never be called.");
}

/**
 * Return the web element used to expand or collapse the tree node element.
 *
 * @return The web element used to expand or collapse the tree node element as {@link BrowserElement}.
 */
protected BrowserElement getExpansionElement() {
	return (this.expansionLocator != null) ? waitForElement(this.expansionLocator, tinyTimeout(), false /*fail*/) : this.element;
}

/**
 * Return the label of the tree node or tree leaf element.
 *
 * @return The label of the tree node or tree leaf element as {@link String}.
 */
protected String getLabel() {
	return getLabelElement().getText();
}

/**
 * Return the label element of the tree node or tree leaf element.
 *
 * @return The label element of the tree node or tree leaf element as {@link BrowserElement}.
 */
public BrowserElement getLabelElement() {
	return waitForElement(this.labelLocator, timeout(), true /*fail*/, false /*displayed*/);
}

/**
 * Specifies whether a child tree node or tree leaf element is available at a given path.
 *
 * @param path The path to the child tree node or tree leaf.
 *
 * @return <code>true</code> if a child tree node or tree leaf element is available at
 * the given path or <code>false</code> otherwise.
 */
protected boolean isChildElementAvailable(final String path) {
	return isChildElementAvailable(path, false /*fail*/);
}

/**
 * Specifies whether a child tree node or tree leaf element is available at a given path.
 *
 * @param path The path to the child tree node or tree leaf.
 * @param fail Specifies whether to fail if a matching child tree node or tree leaf
 * element is not found.
 *
 * @return <code>true</code> if a child tree node or tree leaf element is available at
 * the given path or <code>false</code> otherwise.
 */
protected boolean isChildElementAvailable(final String path, final boolean fail) {
	return getChildElement(path, fail) != null;
}

@Override
public boolean isExpandable() throws ScenarioFailedError {
	return (this.expansionElement != null) && (!getChildElements().isEmpty());
}

@Override
public boolean isExpanded() throws ScenarioFailedError {
	for (TreeNodeElement webTreeNodeElement : getChildElements()) {
		if(webTreeNodeElement.isDisplayed(false /*recovery*/)) {
			// If at least one child element is visible, then
			// the parent element must have been expanded.
			return true;
		}
	}

	return false;
}
}

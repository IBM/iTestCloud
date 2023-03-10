/*********************************************************************
 * Copyright (c) 2013, 2022 IBM Corporation and others.
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

import static itest.cloud.scenario.ScenarioUtils.*;

import java.util.*;

import org.openqa.selenium.By;

import itest.cloud.pages.Page;
import itest.cloud.scenario.errors.ScenarioFailedError;
import itest.cloud.scenario.errors.WaitElementTimeoutError;

/**
 * Class to handle a folder web element in a web page.  A folder may contain multiple folders,
 * which may be several levels deep.
 * <p>
 * Following API features are available on a folder:
 * <ul>
 * <li>{@link #collapse()}: Collapse the folder.</li>
 * <li>{@link #expand()}: Expand the folder.</li>
 * <li>{@link #findFolder(String)}: Search for a folder name in the tree.</li>
 * <li>{@link #findFolder(String, String)}: Search for a folder name in the tree relatively to a given parent.</li>
 * <li>{@link #findLeaf(String, String)}: Search for a leaf name in the tree relatively to a given parent.</li>
 * <li>{@link #getAllLeaves()}: Return all leaves of the current folder.</li>
 * <li>{@link #getAllLeavesPath()}: Return path for all leaves of the current folder.</li>
 * <li>{@link #getAllNodes()}: Return all nodes of the current folder.</li>
 * <li>{@link #getChildren()}: Returns the folder's children.</li>
 * <li>{@link #getChildrenNodes()}: Return children nodes of the current folder.</li>
 * <li>{@link #getFolder(String)}: Gets the sub-folder for the given path.</li>
 * <li>{@link #getParentFolder()}: Returns the parent folder of the current folder.</li>
 * <li>{@link #getPath()}: Returns the path of the current folder.</li>>
 * <li>{@link #getText()}: Return the text of the expandable element.</li>
 * <li>{@link #searchFolder(String)}: Searches the folder with the given name in the hierarchy starting from current folder.</li>
 * <li>{@link #select()}: Select the folder.</li>
 * <li>{@link #selectFolder(String)}: Selects the sub-folder for the given path.</li>
 * </ul>
 * </p><p>
 * Following <b>internal</b> features are available for a folder:
 * <ul>
 * <li>{@link #createFolderElement(BrowserElement)}: Creates the folder element instance from the given web element.</li>
 * <li>{@link #getChild(String)}: Returns the given child folder.</li>
 * <li>{@link #getChildrenElements()}: Returns the children web elements list.</li>
 * <li>{@link #getContainerElement()}: Returns the web element containing children elements.</li>
 * <li>{@link #getExpandableElement()}: Returns the web element used to expand the folder.</li>
 * <li>{@link #initExpandableElement()}: Initialize the expandable element using the corresponding locator.</li>
 * <li>{@link #isContainer()}: Returns whether the current folder is a container or not.</li>
 * <li>{@link #isLeaf()}: Returns whether the current folder is a leaf or not.</li>
 * </ul>
 */
public class FolderElement extends ElementWrapper {

//	/**
//	 * The parent of the current folder, <code>null</code> for root element.
//	 */
//	final private WebFolderElement parent;

	/**
	 * The text of the current folder
	 */
	private String text;

	/**
	 * The path of the current folder
	 */
	private String path;

	/**
	 * The element used to expand the folder. If <code>null</code>, then it's
	 * assumed that the folder expansion is done by clicking on its web element.
	 */
	protected ExpandableElement expandableElement;

	/**
	 * The element used to get the folder children. If <code>null</code>, then
	 * subclass has to override {@link #getChildrenElements()} method.
	 */
	protected BrowserElement containerElement;

	/**
	 * The search mechanism to find the expandable element.
	 * <p>
	 * If <code>null</code>, then it's assumed there's no specific element to
	 * expand the folder and selecting it (ie. clicking on it) is enough to expand
	 * and collapse it.
	 */
	protected final By expandBy;

	/**
	 * The search mechanism to find the web element to expand/collapse the
	 * expandable element if any. If <code>null</code>, then it's assumed
	 * there's no specific element for the expandable element expansion and
	 * selecting it (ie. clicking on it) is enough to expand and collapse it.
	 */
	protected final By expansionBy;

	/**
	 * The search mechanism to find the parent element of the folder's children
	 * elements. If <code>null</code>, then {@link #getChildrenElements()}
	 * method has to be overridden.
	 */
	protected final By containerBy;

public FolderElement(final Page page, final By elementBy, final By expandBy, final By expansionBy, final By containerBy) {
	this(page, elementBy, expandBy, expansionBy, containerBy, null);
}

public FolderElement(final Page page, final By elementBy, final By expandBy, final By expansionBy, final By containerBy, final FolderElement parentFolder) {
	super(page, elementBy);
	this.parent = parentFolder;
	this.expandBy = expandBy;
	this.expansionBy = expansionBy;
	this.containerBy = containerBy;
}

public FolderElement(final Page page, final BrowserElement element, final By expandBy, final By expansionBy, final By containerBy) {
	this(page, element, expandBy, expansionBy, containerBy, null);
}

public FolderElement(final Page page, final BrowserElement element, final By expandBy, final By expansionBy, final By containerBy, final FolderElement parentFolder) {
	super(page, element);
	this.parent = parentFolder;
	this.expandBy = expandBy;
	this.expansionBy = expansionBy;
	this.containerBy = containerBy;
}

public FolderElement(final Page page, final BrowserElement element, final FolderElement parentFolder) {
	super(page, element);
	this.parent = parentFolder;
	this.expandBy = parentFolder.expandBy;
	this.expansionBy = parentFolder.expansionBy;
	this.containerBy = parentFolder.containerBy;
}

/**
 * Collapse the folder.
 */
public void collapse() {
	getExpandableElement();
	if (this.expandableElement != null && isContainer()) {
		this.expandableElement.collapse();
	}
}

/**
 * Creates the folder element instance from the given web element.
 *
 * @param childElement The folder web element
 * @return The folder element as a {@link FolderElement}.
 */
protected FolderElement createFolderElement(final BrowserElement childElement) {
	return new FolderElement(getPage(), childElement, this);
}

/**
 * Expand the folder.
 */
public void expand() {
	getExpandableElement();
	if (this.expandableElement != null) {
		this.expandableElement.expand();
	}
}

private FolderElement find(final String parentName, final String folder, final boolean leaf) {

	// Get the children
	List<FolderElement> children = getChildren();

	// Look for the searched folder
	for (FolderElement child : children) {
		String childFolderName = child.getText();
	    boolean container = child.isContainer();
	    if (childFolderName.equals(folder) && (parentName == null || getText().equals(parentName))) {
	    	if (!(leaf  && container)) {
		    	return child;
	    	}
	    }
		if (container) {
	    	FolderElement folderElement = child.find(parentName, folder, leaf);
	    	if (folderElement != null) {
	    		return folderElement;
	    	}
	    }
    }

	// No match found.
	return null;
}

/**
 * Search for a folder name in the tree.
 * <p>
 * The search is performed depth first.
 * </p><p>
 * Note that if there are several folders with the same name, then first found is
 * returned.
 * </p><p>
 *
 * @param folder Name of the child folder to search
 * @return The matching child folder as a {@link FolderElement} or
 * <code>null</code> if current folder has no child with the given name.
 */
public FolderElement findFolder(final String folder) {
	return findFolder(null, folder);
}

/**
 * Search for a folder name in the tree relatively to an optional parent.
 * <p>
 * The search is performed depth first.
 * </p>
 * @param parentFolder The parent folder which contains the folder. If <code>null</code>
 * then the first folder in the tree with the given name will be returned.
 * @param folder The folder name to search
 * @return The matching folder as a {@link FolderElement} or
 * <code>null</code> if none was found.
 */
public FolderElement findFolder(final String parentFolder, final String folder) {
	return find(parentFolder, folder, false/*leaf*/);
}

/**
 * Search for a leaf name in the tree relatively to an optional parent.
 * <p>
 * The search is performed depth first.
 * </p>
 * @param parentFolder The parent folder which contains the leaf. If <code>null</code>
 * then the first leaf in the tree with the given name will be returned.
 * @param leaf Name of the leaf to search
 * @return The matching leaf as a {@link FolderElement} or
 * <code>null</code> if none was found.
 */
public FolderElement findLeaf(final String parentFolder, final String leaf) {
	return find(parentFolder, leaf, true/*leaf*/);
}

/**
 * Return all leaves of the current folder.
 *
 * @return All folder leaves as a {@link List} of {@link FolderElement}.
 */
public List<FolderElement> getAllLeaves() {

	// Init list
	List<FolderElement> allLeaves = new ArrayList<FolderElement>();

	// Get the children
	List<FolderElement> children = getChildren();

	// Complete the all leaves list
	for (FolderElement child : children) {
		if (child.isContainer()) {
	    	allLeaves.addAll(child.getAllLeaves());
	    } else {
	    	child.element.makeVisible();
	    	child.getPath();
	    	allLeaves.add(child);
	    }
    }

	// Return the list.
	return allLeaves;
}

/**
 * Return path for all leaves of the current folder.
 *
 * @return All paths as a {@link List} of {@link String}.
 */
public List<String> getAllLeavesPath() {

	// Init list
	List<String> allPaths = new ArrayList<String>();

	// Get the children
	List<FolderElement> children = getChildren();

	// Complete the all leaves list
	for (FolderElement child : children) {
		if (child.isContainer()) {
	    	allPaths.addAll(child.getAllLeavesPath());
	    } else {
	    	child.element.makeVisible();
	    	allPaths.add(child.getPath());
	    }
    }

	// Return the list.
	return allPaths;
}

/**
 * Return all nodes of the current folder.
 *
 * @return All nodes as a {@link List} of {@link String}.
 */
public List<String> getAllNodes() {

	// Init list
	List<String> allNodes = new ArrayList<String>();

	// Get the children
	List<FolderElement> children = getChildren();

	// Complete the all leaves list
	for (FolderElement child : children) {
		child.element.makeVisible();
    	allNodes.add(child.getText());

		if (child.isContainer()) {
	    	allNodes.addAll(child.getAllNodes());
	    }
    }

	// Return the list.
	return allNodes;
}

/**
 * Returns the folder's child matching the given name.
 *
 * @param folder	Name of the child folder.
 * @return The matching child folder as a {@link FolderElement} or
 * <code>null</code> if current folder has no child with the given name.
 */
protected FolderElement getChild(final String folder) {
	// Get the children.
	List<FolderElement> children = getChildren();
	for (FolderElement child : children) {
		String childFolderName = child.getText();
	    if (childFolderName.equals(folder)) {
	    	return child;
	    }
    }

	// No match found.
	return null;
}

/**
 * Returns the folder's children.
 *
 * @return The list of children as a {@link List} of {@link FolderElement}.
 */
final public List<FolderElement> getChildren() {
	// Expand node first
	expand();
	// Look for children elements
	if (getContainerElement().isDisplayed()) {
		List<BrowserElement> childrenElements = getChildrenElements();
		List<FolderElement> childrenFolderElements = new ArrayList<FolderElement>(childrenElements.size());
		for (BrowserElement childElement : childrenElements) {
			childrenFolderElements.add(createFolderElement(childElement));
	    }
		return childrenFolderElements;
	}
	return Collections.emptyList();
}

/**
 * Returns the children web elements list.
 *
 * @return The children as a {@link List} of {@link BrowserElement}.
 */
protected List<BrowserElement> getChildrenElements() {
	return getContainerElement().getChildren();
}

/**
 * Return children nodes of the current folder.
 *
 * @return Children nodes as a {@link List} of {@link String}.
 */
public List<String> getChildrenNodes() {

	// Get the children
	List<FolderElement> children = getChildren();
	List<String> childrenNodes = new ArrayList<String>(children.size());

	// Complete the all leaves list
	for (FolderElement child : children) {
    	child.element.makeVisible();
    	childrenNodes.add(child.getText());
    }

	// Return the list.
	return childrenNodes;
}

/**
 * Returns the web element containing children elements.
 *
 * @return The container web element as a {@link BrowserElement}.
 */
protected BrowserElement getContainerElement() {
	if (this.containerElement == null) {
		if (this.containerBy == null) {
			this.containerElement = this.element;
		} else {
			this.containerElement = waitForElement(this.containerBy);
		}
	}
	return this.containerElement;
}

/**
 * Returns the web element used to expand the folder.
 *
 * @return The web element as a {@link BrowserElement}.
 */
final protected ExpandableElement getExpandableElement() {
	if (this.expandableElement == null && this.expandBy != null) {
		initExpandableElement();
	}
	return this.expandableElement;
}

/**
 * Gets the sub-folder for the given path.
 * <p>
 * If path has several segments, then the search occurs recursively through
 * sub-folders levels to match <b>each</b> segment of the given path.
 * </p><p>
 * For example, "JKE Banking/JKE Enterprise Project/Business Goals" accesses the
 * Business Goals sub-sub-folder. To access the top folder or project folder just
 * enter the folder name, ex. "JKE Banking".
 * </p>
 * @param folderPath The folder path. Might be a simple name or a fully-qualified
 * path using '/' character for segment delimiter.
 * @return The selected folder as a {@link FolderElement} or
 * <code>null</code> if no folder was not found for the given folder path.
 */
public FolderElement getFolder(final String folderPath) {

	// Get path first segment
	int start = folderPath.startsWith(PATH_SEPARATOR) ? 1 : 0;
	int idx = folderPath.indexOf(PATH_SEPARATOR, start);
	String childName = idx < 0 ? folderPath : folderPath.substring(start, idx);

	// Search for matching folder's child.
	FolderElement childFolder = getChild(childName);

	// Return if on last segment or no child has been found
	if (idx < 0 || childFolder == null) {
		return childFolder;
	}

	// Recurse to sub-level
	return childFolder.getFolder(folderPath.substring(idx+1));
}

/**
 * Returns the parent folder of the current folder.
 *
 * @return The folder as a {@link FolderElement} or <code>null</code>
 * if current folder is the root.
 */
public FolderElement getParentFolder() {
	return (FolderElement) this.parent;
}

/**
 * Returns the path of the current folder.
 * <p>
 * The path is made of all containing folders texts separated by /'s plus the folder
 * text (e.g. "/Custom Reports/Sample Report Definitions/CCM").
 * </p>
 * TODO: It seems to be a really costly operation, needs to be improved...
 */
public String getPath() {
	if (this.path == null) {
		if (this.parent == null) {
			return EMPTY_STRING;
		}
		StringBuilder pathBuilder = new StringBuilder();
		pathBuilder.append(getParentFolder().getPath());
		pathBuilder.append('/').append(getText());
		this.path = pathBuilder.toString();
	}
	return this.path;
}

/**
 * {@inheritDoc}
 * <p>
 * Overrides to return the text of the expandable element.
 * </p>
 */
@Override
public String getText() {
	if (this.text == null) {
		this.text = getExpandableElement().getText();
	}
	return this.text;
}

/**
 * Initialize the expandable element using the corresponding locator.
 */
protected void initExpandableElement() {
	BrowserElement expandElement = waitForElement(this.expandBy);
	this.expandableElement = new ExpandableElement(getPage(), expandElement, this.expansionBy);
}

/**
 * Returns whether the current folder is a container or not.
 *
 * @return <code>true</code> if the current folder is a container, <code>false</code>
 * if it's a leaf.
 */
protected boolean isContainer() {
	getExpandableElement();
	if (this.expandableElement != null) {
		return this.expandableElement.isExpandable();
	}
	throw new ScenarioFailedError("Class "+getClassSimpleName(getClass())+" should override isContainer() method.");
}

/**
 * Returns whether the current folder is a leaf or not.
 *
 * @return <code>true</code> if the current folder is a leaf, <code>false</code>
 * if it's a container.
 */
protected boolean isLeaf() {
	return !isContainer();
}

/**
 * Searches the folder with the given name in the hierarchy starting from current
 * folder.
 * <p>
 * There's no indication if the returned folder is the unique one with the given
 * name in the hierarchy, as the first matching folder (deepest first) is returned.
 * </p>
 *
 * @param folder The folder to search in the hierarchy. That might be either a name
 * or a path (absolute or relative).
 * @return The first folder matching the given name as a {@link FolderElement}
 * or <code>null</code> if none was found.
 */
public FolderElement searchFolder(final String folder) {

	// Check whether the folderPath includes separators
	int idx = folder.indexOf(PATH_SEPARATOR);
	if (idx >= 0) {
		String firstFolderName = folder.substring(0, idx);
		if (firstFolderName.equals(getText())) {
			return getFolder(firstFolderName.substring(idx+1));
		}
		return getFolder(folder);
	}

	// Recurse to find the folder name
	List<FolderElement> children = getChildren();

	// Check children first
	for (FolderElement child : children) {
		if (child.getText().equals(folder)) {
			return child;
		}
    }

	// Recurse sub-levels if still not found
	for (FolderElement child : children) {
		if (child.isContainer()) {
	    	FolderElement searchedFolder = child.searchFolder(folder);
	    	if (searchedFolder != null) {
	    		return searchedFolder;
    		}
		}
    }

	// No folder was found.
	return null;
}

/**
 * Select the folder.
 */
public void select() {
	getExpandableElement();
	if (this.expandableElement != null) {
		this.expandableElement.element.click();
	} else {
		throw new ScenarioFailedError("Class "+getClassSimpleName(getClass())+" should override select() method.");
	}
}

/**
 * Selects the sub-folder for the given path.
 * <p>
 * If path has several segments, then the search occurs recursively through
 * sub-folders levels to match <b>each</b> segment of the given path.
 * </p><p>
 * For example, "JKE Banking/JKE Enterprise Project/Business Goals" accesses the
 * Business Goals sub-folder.  To access the top folder or project folder just
 * enter the folder name, ex. "JKE Banking".
 * </p>
 * @param folderPath The folder path. Might be a simple name or a fully-qualified
 * path using '/' character for segment delimiter.
 * @return The selected folder as a {@link FolderElement}.
 * @throws ScenarioFailedError if no folder was not found for the given folder path.
 */
public FolderElement selectFolder(final String folderPath) throws ScenarioFailedError {
	FolderElement folderElement = getFolder(folderPath);
	if (folderElement == null) {
		throw new WaitElementTimeoutError("No folder '"+folderPath+" has been found in the current folder tree.");
	}
	folderElement.select();
	return folderElement;
}
}

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
package itest.cloud.ibm.page.element;

import static java.lang.Boolean.parseBoolean;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.page.Page;
import itest.cloud.page.element.*;

/**
 * This class represents a generic list of tabs and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this element:
 * <ul>
 * <li>{@link #isOpen(Pattern)}: Specifies whether a given tab is the currently opened tab.</li>
 * </ul>
 * </p>
 */
public class IbmTabListElement extends TabListElement {

public IbmTabListElement(final ElementWrapper parent, final BrowserElement element, final By openLinkLocator) {
	super(parent, element, openLinkLocator);
}

public IbmTabListElement(final ElementWrapper parent, final By locator, final By openLinkLocator) {
	super(parent, locator, openLinkLocator);
}

public IbmTabListElement(final Page page, final BrowserElement element, final By openLinkLocator) {
	super(page, element, openLinkLocator);
}

public IbmTabListElement(final Page page, final By locator, final By openLinkLocator) {
	super(page, locator, openLinkLocator);
}

public IbmTabListElement(final Page page) {
	super(page, By.xpath("//*[@role='tablist']"), By.xpath(".//*[@role='tab']"));
}

/**
 * Specifies whether a given tab is the currently opened tab.
 *
 * @param pattern A pattern matching the name or label of the tab as {@link Pattern}.
 *
 * @return <code>true</code> if the given is the currently opened tab or
 * <code>false</code> otherwise.
 */
@Override
protected boolean isOpen(final Pattern pattern) {
	final BrowserElement openLinkElement = getOpenLinkElement(pattern);

	return parseBoolean(openLinkElement.getAttribute("aria-selected"));
}
}

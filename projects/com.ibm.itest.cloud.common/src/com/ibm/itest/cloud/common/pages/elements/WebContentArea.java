/*********************************************************************
 * Copyright (c) 2012, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.common.pages.elements;

import org.openqa.selenium.By;

import com.ibm.itest.cloud.common.pages.WebPage;
import com.ibm.itest.cloud.common.pages.frames.WebBrowserFrame;

/**
 * Abstract class to manage web page content areas.
 * <p>
 * No specific feature implemented at the basic framework level.
 * </p>
 */
public abstract class WebContentArea extends WebElementWrapper {

public WebContentArea(final WebElementWrapper parent, final By selectBy) {
	super(parent, selectBy);
}

public WebContentArea(final WebElementWrapper parent, final WebBrowserElement element, final WebBrowserFrame frame) {
	super(parent, element, frame);
}

public WebContentArea(final WebElementWrapper parent, final WebBrowserElement element) {
	super(parent, element);
}

public WebContentArea(final WebPage page, final By findBy, final WebBrowserFrame frame) {
	super(page, findBy, frame);
}

public WebContentArea(final WebPage page, final By findBy) {
	super(page, findBy);
}

public WebContentArea(final WebPage page, final WebBrowserElement element, final WebBrowserFrame frame) {
	super(page, element, frame);
}

public WebContentArea(final WebPage page, final WebBrowserElement element) {
	super(page, element);
}

public WebContentArea(final WebPage page, final WebBrowserFrame frame) {
	super(page, frame);
}

public WebContentArea(final WebPage page) {
	super(page);
}
}

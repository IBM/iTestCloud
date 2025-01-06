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
package itest.cloud.ibm.page.element.wxbi.metric;

import static itest.cloud.scenario.ScenarioUtil.println;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.dialog.wxbi.WxbiKeyMetricDialog;
import itest.cloud.ibm.page.element.IbmElementWrapper;
import itest.cloud.ibm.page.element.wxbi.WxbiLiveWidgetElement;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;

/**
 * This class defines and manages a key metric element in {@link WxbiCarouselElement}.
 * <p>
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #getLiveWidgetElement()}: Return the live widget element associated with the visualization element.</li>
 * <li>{@link #getName()}: Return the name of the key metric.</li>
 * <li>{@link #openKeyMetricDialog()}: Open the key metric dialog.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #waitForLoadingEnd()}: Wait for loading of the wrapped element to complete.</li>
 * </ul>
 * </p>
 */
public class WxbiKeyMetricElement extends IbmElementWrapper {

	public static final String KEY_METRIC_ELEMENT_LOCATOR_STRING = ".//*[contains(@class,'ripasso-carousel-item')]";
	public static final By KEY_METRIC_ELEMENT_LOCATOR = By.xpath(KEY_METRIC_ELEMENT_LOCATOR_STRING);
	public static final String KEY_METRIC_ELEMENT_TITLE_LOCATOR_STRING = ".//*[contains(@class,'title-value')]";

public WxbiKeyMetricElement(final ElementWrapper parent, final BrowserElement element) {
	super(parent, element);
	this.data = new String[] {getTitleElement().getText()};
}

public WxbiKeyMetricElement(final ElementWrapper parent, final BrowserElement element, final String... data) {
	super(parent, element, data);
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote(getName()));
}

/**
 * Return the live widget element associated with the key metric element.
 *
 * @return The live widget element associated with the key metric element as {@link WxbiLiveWidgetElement}.
 */
public WxbiLiveWidgetElement getLiveWidgetElement() {
	WxbiLiveWidgetElement liveWidgetElement = new WxbiLiveWidgetElement(this);
	// Wait for the visualization to load.
	liveWidgetElement.waitForLoadingEnd();

	return liveWidgetElement;
}

/**
 * Return the name of the key metric.
 *
 * @return The name of the key metric as {@link String}.
 */
public String getName() {
	return this.data[0];
}

@Override
protected By getTitleElementLocator() {
	return By.xpath(KEY_METRIC_ELEMENT_TITLE_LOCATOR_STRING);
}

/**
 * Open the key metric dialog.
 *
 * @return The Key Metric dialog opened as {@link WxbiKeyMetricDialog}.
 */
public WxbiKeyMetricDialog openKeyMetricDialog() {
	final WxbiKeyMetricDialog dialog = new WxbiKeyMetricDialog(getPage(), getName());

	if(dialog.isOpened()) {
		println("	  -> The key metric '" + getName() + "' was already open and therefore, no attempt was made to open it.");
		dialog.opened();
		return dialog;
	}

	dialog.open(getTitleElement());
	return dialog;
}

@Override
public void waitForLoadingEnd() {
	super.waitForLoadingEnd();
	// Wait for the visualization to load.
	getLiveWidgetElement();
}
}
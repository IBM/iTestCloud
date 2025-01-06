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
package itest.cloud.ibm.page.dialog.wxbi;

import static itest.cloud.ibm.scenario.wxbi.WxbiScenarioUtil.getSuggestedQuestionElementLocator;
import static itest.cloud.performance.PerfManager.PERFORMANCE_ENABLED;
import static java.util.regex.Pattern.compile;
import static java.util.regex.Pattern.quote;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.dialog.IbmDialog;
import itest.cloud.ibm.page.element.wxbi.WxbiVisualizationElement;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.performance.PerfManager.RegressionType;

/**
 * This class represents the Key Metric dialog and manages all its common actions.
 * <p>
 * Following public features are accessible on this page:
 * <ul>
 * <li>{@link #askQuestion()}: Ask a question on the key metric.</li>
 * <li>{@link #askQuestion(int)}: Ask a suggested question at a given index.</li>
 * <li>{@link #askQuestion(String)}: Ask a given suggested question.</li>
 * <li>{@link #getSuggestedQuestions()}: Return the suggested questions of the key metric.</li>
 * <li>{@link #getVisualizationElement()}: Return the visualization element associated with the message.</li>
 * </ul>
 * </p><p>
 * Following private features are also defined or specialized by this page:
 * <ul>
 * <li>{@link #getCloseButton(boolean)}: Return the xpath of the button to close the window.</li>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #waitForLoadingEnd()}: Wait for loading of the element to complete.</li>
 * </ul>
 * </p>
 */
public class WxbiKeyMetricDialog extends IbmDialog {

public WxbiKeyMetricDialog(final Page page, final String... data) {
	super(page, data);
}

/**
 * Ask a question on the key metric.
 * <p>
 * The dialog will be automatically dismissed at the conclusion of this method.
 * </p>
 */
public void askQuestion() {
	close();
}

/**
 * Ask a suggested question at a given index.
 * <p>
 * The dialog will be automatically dismissed at the conclusion of this method.
 * </p>
 *
 * @param index The index of the suggested question.
 */
public void askQuestion(final int index) {
	askQuestion(null /*question*/, index);
}

/**
 * Ask a given suggested question.
 * <p>
 * The dialog will be automatically dismissed at the conclusion of this method.
 * </p>
 *
 * @param question The suggested question as {@link String}.
 */
public void askQuestion(final String question) {
	askQuestion(question, -1 /*index*/);
}

private void askQuestion(final String question, final int index) {
	// Start server time if performances are managed
	if (PERFORMANCE_ENABLED) {
		this.page.startPerfManagerServerTimer();
	}

	// Perform any actions prior to closing the window
	preCloseActions();

	// Perform the close action
	getSuggestedQuestionElement(question, index, true /*fail*/).click();

	// Wait for the window to vanish
	waitWhileDisplayed(closeTimeout());

	// Add performance result
	if (PERFORMANCE_ENABLED) {
		this.page.addPerfResult(RegressionType.SERVER, this.page.getTitle());
	}
}

@Override
protected String getCloseButton(final boolean validate) {
	return ".//button[" + (validate ? "text()='Ask a question'" : "contains(@class,'modal-close')") + "]";
}

@Override
protected Pattern getExpectedTitle() {
	return compile(quote(getName()));
}

/**
 * Return the name of the visualization.
 *
 * @return The name of the visualization as {@link String}.
 */
public String getName() {
	return this.data[0];
}

/**
 * Return a given suggested question element.
 *
 * @param question The suggested question as {@link String}.
 * @param index The index of the suggested question.
 * @param fail Specifies whether to fail if a matching suggested question element could not be found.
 *
 * @return The suggested question element with the given name as {@link BrowserElement} or
 * <code>null</code> if the given suggested question could not be found and instructed not to fail in such a situation.
 */
private BrowserElement getSuggestedQuestionElement(final String question, final int index, final boolean fail) {
	final By locator = (question != null) ? getSuggestedQuestionElementLocator(question) : getSuggestedQuestionElementLocator(index);
	return waitForElement(locator, fail ? timeout() : tinyTimeout(), fail);
}

/**
 * Return the suggested questions of the key metric.
 *
 * @return The suggested questions of the key metric as {@link List} of {@link String}.
 */
public List<String> getSuggestedQuestions() {
	final List<BrowserElement> suggestedQuestionWebElements = waitForElements(getSuggestedQuestionElementLocator(null /*question*/));
	final List<String> suggestedQuestionElements = new ArrayList<String>(suggestedQuestionWebElements.size());

	for (BrowserElement suggestedQuestionWebElement : suggestedQuestionWebElements) {
		suggestedQuestionElements.add(suggestedQuestionWebElement.getText());
	}

	return suggestedQuestionElements;
}

/**
 * Return the  visualization element associated with the message.
 *
 * @return The visualization element as {@link WxbiVisualizationElement}.
 */
public WxbiVisualizationElement getVisualizationElement() {
	WxbiVisualizationElement visualizationElement = new WxbiVisualizationElement(this);
	// Wait for the visualization element in this dialog to load.
	visualizationElement.waitForLoadingEnd();

	return visualizationElement;
}

@Override
public void waitForLoadingEnd() {
	super.waitForLoadingEnd();
	// Wait for the visualization element in this dialog to load.
	getVisualizationElement();
	// Wait for the suggested question elements in this dialog to load.
	waitForElement(getSuggestedQuestionElementLocator(null /*question*/), timeout(), true /*fail*/, true /*displayed*/, false /*single*/);
}
}
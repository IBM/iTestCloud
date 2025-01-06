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
package itest.cloud.ibm.test.step.wxbi;

import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import itest.cloud.annotation.Dependency;
import itest.cloud.ibm.page.element.wxbi.conversation.WxbiConversationElement;
import itest.cloud.ibm.page.element.wxbi.conversation.WxbiConversationsMenuElement;
import itest.cloud.ibm.page.element.wxbi.metric.WxbiCarouselElement;
import itest.cloud.ibm.page.wxbia.WxbiHomePage;
import itest.cloud.ibm.test.scenario.IbmTestScenarioStepRunner;
import itest.cloud.ibm.test.scenario.wxbi.WxbiTestScenarioStep;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This class defines a set of tests to validate the readiness of the UI.
 * <p>
 * The following is a list of tests associated with this test suite:
 * <ul>
 * <li>{@link #testB01_IsConversationsMenuClosedByDefault()}: Validate whether the conversations menu is closed by default.</li>
 * <li>{@link #testB02_OpenConversationsMenu()}: Reopen the conversations menu.</li>
 * <li>{@link #testB03_IsFirstConversationOpenedByDefault()}: Validate whether the latest conversation is open in the conversation editor by default.</li>
 * <li>{@link #testB04_CloseConversationsMenu()}: Close the conversations menu.</li>
 * <li>{@link #testC01_IsKeyMetricsMenuOpenByDefault()}: Validate whether the key metric menu is open by default.</li>
 * <li>{@link #testC02_CloseKeyMetricsMenu()}: Close the key metric menu.</li>
 * <li>{@link #testC03_ReopenKeyMetricsMenu()}: Reopen the key metric menu.</li>
 * <li>{@link #testC04_MaximizeKeyMetricsMenu()}: Maximize the key metric menu.</li>
 * <li>{@link #testC05_ResizeKeyMetricsMenu()}: Resize the key metric menu to its default size.</li>
 * </ul>
 * </p>
 */
@RunWith(IbmTestScenarioStepRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StepB01_UiReadinessTests extends WxbiTestScenarioStep {

	private static final String TEST_IS_CONVERSATIONS_MENU_CLOSED_BY_DEFAULT = CLASS_INDICATOR_OF_DEPENDENCY + ".testB01_IsConversationsMenuClosedByDefault";
	private static final String TEST_OPEN_CONVERSATIONS_MENU = CLASS_INDICATOR_OF_DEPENDENCY + ".testB02_OpenConversationsMenu";
	private static final String TEST_IS_KEY_METRICS_MENU_OPEN_BY_DEFAULT = CLASS_INDICATOR_OF_DEPENDENCY + ".testC01_IsKeyMetricsMenuOpenByDefault";
	private static final String TEST_CLOSE_KEY_METRICS_MENU = CLASS_INDICATOR_OF_DEPENDENCY + ".testC02_CloseKeyMetricsMenu";
	private static final String TEST_REOPEN_KEY_METRICS_MENU = CLASS_INDICATOR_OF_DEPENDENCY + ".testC03_ReopenKeyMetricsMenu";
	private static final String TEST_MAXIMIZE_KEY_METRICS_MENU = CLASS_INDICATOR_OF_DEPENDENCY + ".testC04_MaximizeKeyMetricsMenu";

private WxbiConversationsMenuElement getConversationsMenuElement() {
	final WxbiHomePage page = openHomePage(getData().getTestUser());
	return page.getConversationsMenuElement();
}

private WxbiCarouselElement getKeyMetricsMenuElement() {
	final WxbiHomePage page = openHomePage(getData().getTestUser());
	return page.getCarouselElement();
}

/**
 * Validate whether the conversations menu is closed by default.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void testB01_IsConversationsMenuClosedByDefault() {
	final WxbiConversationsMenuElement conversationsMenuElement = getConversationsMenuElement();
	if(conversationsMenuElement.isExpanded()) throw new ScenarioFailedError("The conversations menu is not closed by default.");
}

/**
 * Reopen the conversations menu.
 */
@Test
@Dependency({TEST_IS_CONVERSATIONS_MENU_CLOSED_BY_DEFAULT})
public void testB02_OpenConversationsMenu() {
	final WxbiConversationsMenuElement conversationsMenuElement = getConversationsMenuElement();
	conversationsMenuElement.expand();
}

/**
 * Validate whether the latest conversation is open in the conversation editor by default.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void testB03_IsFirstConversationOpenedByDefault() {
	final List<WxbiConversationElement> conversationElements = getConversationElements(getData().getTestUser());
	final WxbiConversationElement latestConversationElement = conversationElements.get(0 /*index*/);
	final WxbiHomePage page = (WxbiHomePage)getCurrentPage();
	final String latestConversation = latestConversationElement.getName();
	final String openedConversation = page.getConversationEditorElement().getTitle();

	if(!latestConversation.equals(openedConversation)) {
		throw new ScenarioFailedError("The latest conversation '" + latestConversation + "' was expected to open in the conversation editor, but the conversation '" + openedConversation + "' was open instead");
	}
}

/**
 * Close the conversations menu.
 */
@Test
@Dependency({TEST_OPEN_CONVERSATIONS_MENU})
public void testB04_CloseConversationsMenu() {
	final WxbiConversationsMenuElement conversationsMenuElement = getConversationsMenuElement();
	conversationsMenuElement.collapse();
}

/**
 * Validate whether the key metric menu is open by default.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void testC01_IsKeyMetricsMenuOpenByDefault() {
	final WxbiCarouselElement keyMetricsMenuElement = getKeyMetricsMenuElement();
	if(!keyMetricsMenuElement.isExpanded()) throw new ScenarioFailedError("The key metrics menu is not open by default.");
}

/**
 * Close the key metric menu.
 */
@Test
@Dependency({TEST_IS_KEY_METRICS_MENU_OPEN_BY_DEFAULT})
public void testC02_CloseKeyMetricsMenu() {
	final WxbiCarouselElement keyMetricsMenuElement = getKeyMetricsMenuElement();
	keyMetricsMenuElement.collapse();
}

/**
 * Reopen the key metric menu.
 */
@Test
@Dependency({TEST_CLOSE_KEY_METRICS_MENU})
public void testC03_ReopenKeyMetricsMenu() {
	final WxbiCarouselElement keyMetricsMenuElement = getKeyMetricsMenuElement();
	keyMetricsMenuElement.expand();
}

/**
 * Maximize the key metric menu.
 */
@Test
@Dependency({TEST_REOPEN_KEY_METRICS_MENU})
public void testC04_MaximizeKeyMetricsMenu() {
	final WxbiCarouselElement keyMetricsMenuElement = getKeyMetricsMenuElement();
	keyMetricsMenuElement.maximize();
}

/**
 * Resize the key metric menu to its default size.
 */
@Test
@Dependency({TEST_MAXIMIZE_KEY_METRICS_MENU})
public void testC05_ResizeKeyMetricsMenu() {
	final WxbiCarouselElement keyMetricsMenuElement = getKeyMetricsMenuElement();
	keyMetricsMenuElement.restore();
}
}
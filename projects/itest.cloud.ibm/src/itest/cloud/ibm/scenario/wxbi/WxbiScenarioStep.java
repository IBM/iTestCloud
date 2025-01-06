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
package itest.cloud.ibm.scenario.wxbi;

import static itest.cloud.ibm.entity.AlertStatus.Success;
import static itest.cloud.scenario.ScenarioUtil.println;

import java.util.List;

import itest.cloud.config.IUser;
import itest.cloud.ibm.entity.wxbi.conversation.WxbiQuestionAnswer;
import itest.cloud.ibm.entity.wxbi.conversation.WxbiVisualizationType;
import itest.cloud.ibm.page.dialog.wxbi.WxbiKeyMetricDialog;
import itest.cloud.ibm.page.element.IbmAlertElement;
import itest.cloud.ibm.page.element.wxbi.WxbiLiveWidgetElement;
import itest.cloud.ibm.page.element.wxbi.WxbiVisualizationElement;
import itest.cloud.ibm.page.element.wxbi.conversation.*;
import itest.cloud.ibm.page.element.wxbi.metric.WxbiCarouselElement;
import itest.cloud.ibm.page.element.wxbi.metric.WxbiKeyMetricElement;
import itest.cloud.ibm.page.wxbia.WxbiHomePage;
import itest.cloud.ibm.page.wxbia.WxbiPage;
import itest.cloud.ibm.page.wxbia.modeling.WxbiModelingPage;
import itest.cloud.ibm.scenario.IbmScenarioStep;
import itest.cloud.ibm.scenario.error.InvalidQuestionError;
import itest.cloud.ibm.topology.IbmTopology;
import itest.cloud.ibm.topology.WxbiApplication;
import itest.cloud.scenario.error.ScenarioFailedError;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * Manage a scenario step.
 * <p>
 * The step gives access to the current location which is either the page location or the current browser URL
 * in case no page was already stored.
 * </p>
 * <p>
 * Following actions are accessible in this page:
 * <ul>
 * </ul>
 * </p>
 * <p>
 * It also contains some other useful helper methods:
 * <ul>
 * <li>{@link #changeUser(IUser)}: Login as a new user.</li>
 * </ul>
 * </p>
 */
public class WxbiScenarioStep extends IbmScenarioStep {

/**
 * Create a conversation.
 *
 * @param name The name of the conversation to be created.
 * @param force Specifies whether to delete and recreate the conversation if a matching conversation is found.
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The newly created conversation element as {@link WxbiConversationElement}.
 */
protected WxbiConversationElement createConversation(final String name, final boolean force, final IUser user) {
	final WxbiConversationElement conversationElement = getConversationElement(name, false /*fail*/, user);

	if(conversationElement != null) {
		if(!force) {
			println("	  -> A conversation with the name '" + name + "' already existed and therefore, reused for this scenario.");
			return conversationElement;
		}
		deleteConversation(name, user);
	}

	final WxbiConversationsMenuElement conversationsMenuElement = openConversationsMenuElement(user);
	final WxbiConversationElement conversationsElement = conversationsMenuElement.createConversation();

	// Rename the newly created conversation to the given and return the renamed element.
	return renameConversation(conversationsElement, name);
}

/**
 * Create a key metric in the carousel from a given visualization in an answer.
 *
 * @param conversation The name of the conversation as {@link String}.
 * @param visualization The name of the visualization as {@link String}.
 * @param visualizationType The type of the visualization to create the key metric from as {@link WxbiVisualizationType}.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The new created key metric element as {@link WxbiKeyMetricElement}.
 */
protected WxbiKeyMetricElement createKeyMetric(final String conversation, final String visualization, final WxbiVisualizationType visualizationType, final boolean force, final IUser user) {
	// Check if the given key metric already exists.
	WxbiKeyMetricElement keyMetricElement = getKeyMetricElement(visualization, false /*fail*/, user);
	if(keyMetricElement != null) {
		// If reached here, it implies that the key metric already exists in the carousel.
		// Reuse the existing key metric if specified to do so in this situation.
		if(!force) {
			println("	  -> A key metric with the name '" + conversation + "' already existed in the carousel and therefore, reused for this scenario.");
			return keyMetricElement;
		}

		// Unpin the existing key metric from the carousel if specified to do so in this situation.
		unpinKeyMetric(visualization, user);
	}

	// Pin the given visualization in the carousel.
	final WxbiConversationEditorElement conversationEditorElement = openConversation(conversation, user);
	final WxbiMessageElement messageElement = conversationEditorElement.getMessageElementWithVisualization(visualization);
	final WxbiVisualizationElement visualizationElement = messageElement.getVisualizationElement(true /*fail*/);
	visualizationElement.openVisualization(visualizationType);
	visualizationElement.pin();

	final WxbiPage page = getCurrentPage();

//	// Wait for an alert to appear indicating that the pining was successful or not.
//	final WxbiPage page = getCurrentPage();
//	final IbmAlertElement alertElement = page.getAlertElement(true /*fail*/);
//	// Make sure the alert indicates that the pining was successful.
//	if(alertElement != null) {
//		if(alertElement.getStatus() != Success) throw new WaitElementTimeoutError("The pinning visualization '" + visualization + "' in the conversation '" + conversation + "' failed with the following message: " + alertElement.getAlert());
//		// Dismiss the alert.
//		alertElement.close();
//	}

	// Check if the unpinned key metric is no longer in the carousel.
	final int timeout = page.timeout();
	final long timeoutMillis = timeout * 1000 + System.currentTimeMillis();
	while ((keyMetricElement = getKeyMetricElement(visualization, false /*fail*/, user)) == null) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("The pinning visualization '" + visualization + "' in the conversation '" + conversation + "' did not appear in the carousel before the timeout '" + timeout + "'s had reached.");
		}
		// Workaround for a pinned visualization not appearing in the carousel until the page is refreshed.
		page.refresh();
	}

	// Return the newly created key metric element in the carousel.
	return keyMetricElement;
}

/**
 * Delete a given conversation.
 *
 * @param name The name of the conversation to be deleted.
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 */
protected void deleteConversation(final String name, final IUser user) {
	final WxbiConversationElement conversationElement = getConversationElement(name, false /*fail*/, user);

	if(conversationElement == null) {
		println("	  -> A conversation with the name '" + name + "' did not exist and therefore, no attempt was made to delete it.");
		return;
	}

	final WxbiHomePage page = (WxbiHomePage) getCurrentPage();
	// Dismiss any existing alters if any.
	page.dismissAlerts(false /*fail*/, false /*verbose*/);

	// Delete the conversation.
	conversationElement.delete();
//	// Workaround RIPASSO-1608.
//	page.refresh();

	// Verify whether the conversation has been removed from this element.
	long timeoutMillis = page.timeout() * 1000 + System.currentTimeMillis();
	while (getConversationElement(name, false /*fail*/, user) != null) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("The deleted conversation with the name '" + name + "' had not been removed from the conversations menu before the timeout '" + page.timeout() + "'s had reached.");
		}
	}

	// Wait for an alert to appear indicating that the deletion was successful or not.
	final IbmAlertElement alertElement = page.getAlertElement(false /*fail*/);
	// Make sure the alert indicates that the deletion was successful.
	if(alertElement != null) {
		if(alertElement.getStatus() != Success) throw new ScenarioFailedError("Deleting the conversation with the name '"+ name + "' failed with the following message: " + alertElement.getAlert());
		// Dismiss the alert.
		alertElement.close();
	}

	// Check if the deleted conversation is no longer in the conversation editor.
	final WxbiConversationEditorElement conversationEditorElement = page.getConversationEditorElement();
	timeoutMillis = page.timeout() * 1000 + System.currentTimeMillis();
	while (conversationEditorElement.getConversationName().equals(name)) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("The deleted conversation with the name '" + name + "' remained open in the conversation editor before the timeout '" + page.timeout() + "'s had reached.");
		}
	}
}

private WxbiConversationElement getConversationElement(final String name, final boolean fail, final IUser user) {
	final WxbiConversationsMenuElement conversationsMenuElement = openConversationsMenuElement(user);
	return conversationsMenuElement.getConversationElement(name, fail);
}

/**
 * Return the conversation elements in the conversations menu.
 *
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The conversation elements as {@link List} of {@link WxbiConversationElement}.
 */
protected List<WxbiConversationElement> getConversationElements(final IUser user) {
	final WxbiConversationsMenuElement conversationsMenuElement = openConversationsMenuElement(user);
	return conversationsMenuElement.getConversationElements();
}

/**
 * {@inheritDoc}
 *
 * @return The page as a subclass of {@link WxbiPage}.
 * May be null if no page was stored neither in current test nor in previous one.
 */
@Override
protected WxbiPage getCurrentPage() {
	return (WxbiPage) super.getCurrentPage();
}

private WxbiKeyMetricElement getKeyMetricElement(final String name, final boolean fail, final IUser user) {
	final WxbiHomePage page = openHomePage(user);
	final WxbiCarouselElement carouselElement = page.openCarouselElement();

	return carouselElement.getKeyMetricElement(name, fail);
}

/**
 * Return information about a specific question in the conversation.
 *
 * @param conversation The name of the conversation as {@link String}.
 * @param question The question in the conversation as {@link String}.
 * @param isVisualizationInAnswer Specifies whether a visualization is associated with the answer of the question.
 * @param isAdditionalInformationAboutVisualizationInAnswer Specifies whether additional information about a visualization is expected to appear in the answer.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The information about the question in the conversation as {@link WxbiQuestionAnswer}.
 */
protected WxbiQuestionAnswer getQuestionAnswer(final String conversation, final String question, final boolean isVisualizationInAnswer, final boolean isAdditionalInformationAboutVisualizationInAnswer, final IUser user) {
	final WxbiConversationEditorElement conversationEditorElement = openConversation(conversation, user);

	return conversationEditorElement.getQuestionAnswer(question, isVisualizationInAnswer, isAdditionalInformationAboutVisualizationInAnswer);
}

/**
 * @see IbmTopology#getWxbiApplication()
 */
protected WxbiApplication getWxbiApplication() {
	return getTopology().getWxbiApplication();
}

/**
 * Specifies whether a given key metric is available in the carousel.
 *
 * @param name The name of the key metric as {@link String}.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return <code>true</code> if the given key metric is available in the carousel or <code>false</code> otherwise.
 */
protected boolean isKeyMetricAvailable(final String name, final IUser user) {
	return getKeyMetricElement(name, false /*fail*/, user) != null;
}

/**
 * Perform the logout operation.
 *
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 */
protected void logout(final IUser user) {
	final WxbiPage page = openWxbiWebPage(user);
	page.logout();
}

/**
 * Open a given conversation.
 *
 * @param name The name of the conversation.
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The Conversation Editor element representing the opened conversation as {@link WxbiConversationEditorElement}.
 */
protected WxbiConversationEditorElement openConversation(final String name, final IUser user) {
	final WxbiConversationElement conversationElement = getConversationElement(name, true /*fail*/, user);
	final WxbiHomePage page = (WxbiHomePage) getCurrentPage();

	if(conversationElement.isSelected()) {
		println("	  -> A conversation with the name '" + name + "' was already open and therefore, no attempt was made to open it.");
		return page.getConversationEditorElement();
	}

	return conversationElement.open();
}

private WxbiConversationsMenuElement openConversationsMenuElement(final IUser user) {
	final WxbiHomePage page = openHomePage(user);
	return page.openConversationsMenuElement();
}

/**
 * Open the home page.
 *
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The opened home page as {@link WxbiHomePage}.
 */
protected WxbiHomePage openHomePage(final IUser user) {
	final WxbiPage page = openWxbiWebPage(WxbiHomePage.class, user);

	return (page == null) ? (WxbiHomePage) getCurrentPage() : page.openHomePage();
}

/**
 * Open a given key metric.
 *
 * @param name The name of the key metric as {@link String}.
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The Key Metric dialog opened as {@link WxbiKeyMetricDialog}.
 */
protected WxbiKeyMetricDialog openKeyMetric(final String name, final IUser user) {
	final WxbiKeyMetricElement keyMetricElement = getKeyMetricElement(name, true /*fail*/, user);
	return keyMetricElement.openKeyMetricDialog();
}

/**
 * Open the modeling page.
 *
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The opened modeling page as {@link WxbiModelingPage}.
 */
protected WxbiModelingPage openModelingPage(final IUser user) {
	final WxbiPage page = getCurrentPage();

	if ((page != null) && page instanceof WxbiModelingPage) return (WxbiModelingPage)page;

	return openWebPage(getWxbiApplication().getModelingPageUrl(), getConfig(), user, WxbiModelingPage.class);
}

/**
 * Open a given type of visualization in an answer of a conversation.
 *
 * @param conversation The name of the conversation as {@link String}.
 * @param question The question in the conversation as {@link String}.
 * @param visualizationType The type of the visualization to open as {@link WxbiVisualizationType}.
 * @param isAdditionalInformationAboutVisualizationInAnswer Specifies whether additional information about a visualization is expected to appear in the answer.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The opened live widget element representing the desired visualization type as {@link WxbiLiveWidgetElement}.
 */
protected WxbiLiveWidgetElement openVisualizationInAnswer(final String conversation, final String question, final WxbiVisualizationType visualizationType, final boolean isAdditionalInformationAboutVisualizationInAnswer, final IUser user) {
	final WxbiQuestionAnswer questionAnswer = getQuestionAnswer(conversation, question, true /*isVisualizationInAnswer*/, isAdditionalInformationAboutVisualizationInAnswer, user);
	final WxbiMessageElement chartElement = questionAnswer.getChartElement();
	final WxbiVisualizationElement visualizationElement = chartElement.getVisualizationElement(true /*fail*/);

	return visualizationElement.openVisualization(visualizationType);
}

private WxbiPage openWxbiWebPage(final Class<? extends WxbiPage> webPageClass, final IUser user) {
	// Change the user to the given
	changeUser(user);

	WxbiPage currentPage = getCurrentPage();

	if (currentPage == null) {
		// No previous page was stored and therefore, open the home page.
		currentPage = openWebPage(getWxbiApplication().getHomePageUrl(), getConfig(), user, WxbiHomePage.class);
	}

	// Check whether the current page matches the given one if a specific web page class is provided.
	if ((webPageClass!= null) && webPageClass.isInstance(currentPage) && currentPage.matchPage()) {
		// If so, return null to indicate to the caller that no further
		// actions are required to open the particular web page since
		// the user is already in the correct web page.
		return null;
	}

	return currentPage;
}

/**
 * Open any page of the application.
 *
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return the opened page as a {@link WxbiPage}.
 * If the specified user is already in a page of the application, the current page is returned.
 */
protected WxbiPage openWxbiWebPage(final IUser user) {
	return openWxbiWebPage(null /*webPageClass*/, user);
}

/**
 * Rename a given conversation.
 *
 * @param currentName The current name of the conversation as {@link String}.
 * @param newName The new name of the conversation as {@link String}.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The renamed conversation element as {@link WxbiConversationElement}.
 */
protected WxbiConversationElement renameConversation(final String currentName, final String newName, final IUser user) {
	final WxbiConversationElement conversationElement = getConversationElement(currentName, true /*fail*/, user);

	return renameConversation(conversationElement, newName);
}

private WxbiConversationElement renameConversation(final WxbiConversationElement conversationElement, final String newName) {
	final WxbiHomePage page = (WxbiHomePage) getCurrentPage();
	// Dismiss any existing alters if any.
	page.dismissAlerts(false /*fail*/, false /*verbose*/);

	conversationElement.rename(newName);
//	// Workaround RIPASSO-1587.
//	page.refresh();

	// Check if the renamed conversation is now open in the conversation editor.
	final WxbiConversationEditorElement conversationEditorElement = page.getConversationEditorElement();
	long timeoutMillis = page.timeout() * 1000 + System.currentTimeMillis();
	while (!conversationEditorElement.getConversationName().equals(newName)) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("The renamed conversation '" + newName + "' failed to open in the conversation editor before the timeout '" + page.timeout() + "'s had reached.");
		}
	}

	// Wait for an alert to appear indicating that the renaming was successful or not.
	final IbmAlertElement alertElement = page.getAlertElement(false /*fail*/);
	// Make sure the alert indicates that the renaming was successful.
	if(alertElement != null) {
		if(alertElement.getStatus() != Success) throw new WaitElementTimeoutError("Renaming the conversation to '" + newName + "' failed with the following message: " + alertElement.getAlert());
		// Dismiss the alert.
		alertElement.close();
	}

	return conversationElement;
}

/**
 * Select a given item in the semantic model.
 *
 * @param name The name of the item in the semantic model to select as {@link String}.
 * @param user The user to perform this operation.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 */
protected void selectItemInSemanticModel(final String name, final IUser user) {
	final WxbiModelingPage page = openModelingPage(user);
	page.selectItemInSemanticModel(name);
}

/**
 * Select a given cloud account.
 *
 * @param account The cloud account to select.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 */
protected void setAccount(final String account, final IUser user) {
	final WxbiPage page = openWxbiWebPage(user);
	page.setAccount(account);
}

///**
// * Sets a given theme for the application.
// *
// * @param user The user to perform this operation.
// * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
// *
// * @param theme The theme to be set for application as {@link WxbiaTheme}.
// */
//protected void setTheme(final WxbiaTheme theme, final IUser user) {
//	WxbiaPage page = openWxbiWebPage(null /*webPageClass*/, user);
//	page.setTheme(theme);
//}

/**
 * Submit a question in a conversation.
 * @param conversation The name of the conversation as {@link String}.
 * @param question The question to submit as {@link String}.
 * @param stopProcessingAnswer Specifies whether to stop processing the answer while an answer is being produced.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The question scope object as {@link String} containing references to the associated
 * question and answer elements as {@link WxbiQuestionAnswer}.
 *
 * @throws InvalidQuestionError If the submitted question is invalid.
 */
protected WxbiQuestionAnswer submitQuestion(final String conversation, final String question, final boolean stopProcessingAnswer, final IUser user) {
	return submitQuestion(conversation, question, false /*suggestedQuestion*/, null /*keyMetric*/, false /*removeVisualizationFromQuestion*/, null /*expectedVisualizationTypeInQuestion*/, null /*expectedVisualizationTypeInAnswer*/, false /*isAdditionalInformationAboutVisualizationInAnswer*/, stopProcessingAnswer, user);
}

/**
 * Submit a question in a conversation.
 *
 * @param conversation The name of the conversation as {@link String}.
 * @param question The question to submit as {@link String}.
 * @param isSuggestedQuestion Specifies whether the question is a suggested or a free form one.
 * @param keyMetric The name of the key metric to associate the question to as {@link String}.
 * A visualization of the key metric will be added to the question and posted with the question in the conversation editor.
 * @param removeVisualizationFromQuestion Specifies whether to remove the visualization of the key metric from the question before the
 * question is submitted.
 * @param expectedVisualizationTypeInQuestion The type of visualization is expected to appear in the question.
 * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the question.
 * @param expectedVisualizationTypeInAnswer The type of visualization is expected to appear in the answer.
 * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the answer.
 * @param isAdditionalInformationAboutVisualizationInAnswer Specifies whether additional information about a visualization is expected to appear in the answer.
 * @param stopProcessingAnswer Specifies whether to stop processing the answer while an answer is being produced.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 * @return The question scope object as {@link String} containing references to the associated
 * question and answer elements as {@link WxbiQuestionAnswer}.
 *
 * @throws InvalidQuestionError If the submitted question is invalid.
 */
protected WxbiQuestionAnswer submitQuestion(final String conversation, final String question, final boolean isSuggestedQuestion, final String keyMetric, final boolean removeVisualizationFromQuestion, final WxbiVisualizationType expectedVisualizationTypeInQuestion, final WxbiVisualizationType expectedVisualizationTypeInAnswer, final boolean isAdditionalInformationAboutVisualizationInAnswer, final boolean stopProcessingAnswer, final IUser user) {
	final WxbiConversationEditorElement conversationEditorElement = openConversation(conversation, user);
	// Submit the question and return the answer.
	return conversationEditorElement.submitQuestion(
		question, isSuggestedQuestion, keyMetric, removeVisualizationFromQuestion, expectedVisualizationTypeInQuestion,
		expectedVisualizationTypeInAnswer, isAdditionalInformationAboutVisualizationInAnswer, stopProcessingAnswer);
}

/**
 * Submit a question in a conversation.
 *
 * @param conversation The name of the conversation as {@link String}.
 * @param question The question to submit as {@link String}.
 * @param isSuggestedQuestion Specifies whether the question is a suggested or a free form one.
 * @param keyMetric The name of the key metric to associate the question to as {@link String}.
 * A visualization of the key metric will be added to the question and posted with the question in the conversation editor.
 * @param expectedVisualizationTypeInQuestion The type of visualization is expected to appear in the question.
 * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the question.
 * @param expectedVisualizationTypeInAnswer The type of visualization is expected to appear in the answer.
 * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the answer.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 * @return The question scope object as {@link String} containing references to the associated
 * question and answer elements as {@link WxbiQuestionAnswer}.
 *
 * @throws InvalidQuestionError If the submitted question is invalid.
 */
protected WxbiQuestionAnswer submitQuestion(final String conversation, final String question, final boolean isSuggestedQuestion, final String keyMetric, final WxbiVisualizationType expectedVisualizationTypeInQuestion, final WxbiVisualizationType expectedVisualizationTypeInAnswer, final IUser user) {
	return submitQuestion(conversation, question, isSuggestedQuestion, keyMetric, false /*removeVisualizationFromQuestion*/, expectedVisualizationTypeInQuestion, expectedVisualizationTypeInAnswer, true /*isAdditionalInformationAboutVisualizationInAnswer*/, false /*stopProcessingAnswer*/, user);
}

/**
 * Submit a question in a conversation.
 *
 * @param question The question to submit as {@link String}.
 * @param conversation The name of the conversation as {@link String}.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The question scope object as {@link String} containing references to the associated
 * question and answer elements as {@link WxbiQuestionAnswer}.
 *
 * @throws InvalidQuestionError If the submitted question is invalid.
 */
protected WxbiQuestionAnswer submitQuestion(final String conversation, final String question, final IUser user) {
	return submitQuestion(conversation, question, false /*suggestedQuestion*/, null /*keyMetric*/, false /*removeVisualizationFromQuestion*/, null /*expectedVisualizationTypeInQuestion*/, null /*expectedVisualizationTypeInAnswer*/, false /*isAdditionalInformationAboutVisualizationInAnswer*/, false /*stopProcessingAnswer*/, user);
}

/**
 * Submit a question in a conversation.
 *
 * @param conversation The name of the conversation as {@link String}.
 * @param question The question to submit as {@link String}.
 * @param keyMetric The name of the key metric to associate the question to as {@link String}.
 * A visualization of the key metric will be added to the question and posted with the question in the conversation editor.
 * @param removeVisualizationFromQuestion Specifies whether to remove the visualization of the key metric from the question before the
 * question is submitted.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 * @return The question scope object as {@link String} containing references to the associated
 * question and answer elements as {@link WxbiQuestionAnswer}.
 *
 * @throws InvalidQuestionError If the submitted question is invalid.
 */
protected WxbiQuestionAnswer submitQuestion(final String conversation, final String question, final String keyMetric, final boolean removeVisualizationFromQuestion, final IUser user) {
	return submitQuestion(conversation, question, false /*isSuggestedQuestion*/, keyMetric, removeVisualizationFromQuestion, null /*expectedVisualizationTypeInQuestion*/, null /*expectedVisualizationTypeInAnswer*/, false /*isAdditionalInformationAboutVisualizationInAnswer*/, false /*stopProcessingAnswer*/, user);
}

/**
 * Submit a question in a conversation.
 *
 * @param conversation The name of the conversation as {@link String}.
 * @param question The question to submit as {@link String}.
 * @param keyMetric The name of the key metric to associate the question to as {@link String}.
 * A visualization of the key metric will be added to the question and posted with the question in the conversation editor.
 * @param expectedVisualizationTypeInQuestion The type of visualization is expected to appear in the question.
 * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the question.
 * @param expectedVisualizationTypeInAnswer The type of visualization is expected to appear in the answer.
 * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the answer.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 * @return The question scope object as {@link String} containing references to the associated
 * question and answer elements as {@link WxbiQuestionAnswer}.
 *
 * @throws InvalidQuestionError If the submitted question is invalid.
 */
protected WxbiQuestionAnswer submitQuestion(final String conversation, final String question, final String keyMetric, final WxbiVisualizationType expectedVisualizationTypeInQuestion, final WxbiVisualizationType expectedVisualizationTypeInAnswer, final IUser user) {
	return submitQuestion(conversation, question, false /*isSuggestedQuestion*/, keyMetric, false /*removeVisualizationFromQuestion*/, expectedVisualizationTypeInQuestion, expectedVisualizationTypeInAnswer, true /*isAdditionalInformationAboutVisualizationInAnswer*/, false /*stopProcessingAnswer*/, user);
}

/**
 * Submit a question in a conversation.
 *
 * @param question The question to submit as {@link String}.
 * @param conversation The name of the conversation as {@link String}.
 * @param expectedVisualizationTypeInAnswer The type of visualization is expected to appear in the answer.
 * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the answer.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The question scope object as {@link String} containing references to the associated
 * question and answer elements as {@link WxbiQuestionAnswer}.
 *
 * @throws InvalidQuestionError If the submitted question is invalid.
 */
protected WxbiQuestionAnswer submitQuestion(final String conversation, final String question, final WxbiVisualizationType expectedVisualizationTypeInAnswer, final IUser user) {
	return submitQuestion(conversation, question, false /*suggestedQuestion*/, null /*keyMetric*/, false /*removeVisualizationFromQuestion*/, null /*expectedVisualizationTypeInQuestion*/, expectedVisualizationTypeInAnswer, true /*isAdditionalInformationAboutVisualizationInAnswer*/, false /*stopProcessingAnswer*/, user);
}

/**
 * Submit a suggested question in a conversation.
 *
 * @param conversation The name of the conversation as {@link String}.
 * @param question The question to submit as {@link String}.
 * @param expectedVisualizationTypeInQuestion The type of visualization is expected to appear in the question.
 * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the question.
 * @param stopProcessingAnswer Specifies whether to stop processing the answer while an answer is being produced.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 * @return The question scope object as {@link String} containing references to the associated
 * question and answer elements as {@link WxbiQuestionAnswer}.
 *
 * @throws InvalidQuestionError If the submitted question is invalid.
 */
protected WxbiQuestionAnswer submitSuggestedQuestion(final String conversation, final String question, final String keyMetric, final WxbiVisualizationType expectedVisualizationTypeInQuestion, final boolean stopProcessingAnswer, final IUser user) {
	return submitQuestion(conversation, question, true /*suggestedQuestion*/, keyMetric, false /*removeVisualizationFromQuestion*/, expectedVisualizationTypeInQuestion, null /*expectedVisualizationTypeInAnswer*/, false /*isAdditionalInformationAboutVisualizationInAnswer*/, stopProcessingAnswer, user);
}

/**
 * Submit a suggested question in a conversation.
 *
 * @param conversation The name of the conversation as {@link String}.
 * @param question The question to submit as {@link String}.
 * @param keyMetric The name of the key metric to associate the question to as {@link String}.
 * A visualization of the key metric will be added to the question and posted with the question in the conversation editor.
 * @param expectedVisualizationTypeInQuestion The type of visualization is expected to appear in the question.
 * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the question.
 * @param expectedVisualizationTypeInAnswer The type of visualization is expected to appear in the answer.
 * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the answer.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 * @return The question scope object as {@link String} containing references to the associated
 * question and answer elements as {@link WxbiQuestionAnswer}.
 *
 * @throws InvalidQuestionError If the submitted question is invalid.
 */
protected WxbiQuestionAnswer submitSuggestedQuestion(final String conversation, final String question, final String keyMetric, final WxbiVisualizationType expectedVisualizationTypeInQuestion, final WxbiVisualizationType expectedVisualizationTypeInAnswer, final IUser user) {
	return submitQuestion(conversation, question, true /*suggestedQuestion*/, keyMetric, false /*removeVisualizationFromQuestion*/, expectedVisualizationTypeInQuestion, expectedVisualizationTypeInAnswer, true /*isAdditionalInformationAboutVisualizationInAnswer*/, false /*stopProcessingAnswer*/, user);
}

/**
 * Submit a suggested question in a conversation.
 *
 * @param conversation The name of the conversation as {@link String}.
 * @param question The question to submit as {@link String}.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 *
 * @return The question scope object as {@link String} containing references to the associated
 * question and answer elements as {@link WxbiQuestionAnswer}.
 *
 * @throws InvalidQuestionError If the submitted question is invalid.
 */
protected WxbiQuestionAnswer submitSuggestedQuestion(final String conversation, final String question, final WxbiVisualizationType expectedVisualizationTypeInQuestion, final WxbiVisualizationType expectedVisualizationTypeInAnswer, final IUser user) {
	return submitQuestion(conversation, question, true /*suggestedQuestion*/, null /*keyMetric*/, false /*removeVisualizationFromQuestion*/, expectedVisualizationTypeInQuestion, expectedVisualizationTypeInAnswer, true /*isAdditionalInformationAboutVisualizationInAnswer*/, false /*stopProcessingAnswer*/, user);
}

/**
 * Unpin a given key metric from the carousel.
 *
 * @param name The name of the key metric as {@link String}.
 * @param user The user to perform this operation as {@link IUser}.
 * If <code>null</code> is provided as the value of this parameter, the current user will be utilized to perform this operation.
 */
protected void unpinKeyMetric(final String name, final IUser user) {
	// Check if the given key metric already exists.
	final WxbiKeyMetricElement keyMetricElement = getKeyMetricElement(name, false /*fail*/, user);

	if(keyMetricElement == null) {
		println("	  -> A key metric with the name '" + name + "' did not exist and therefore, no attempt was made to delete it.");
		return;
	}

	// Unpin the existing key metric from the carousel if specified to do so in this situation.
	final WxbiKeyMetricDialog keyMetricDialog = keyMetricElement.openKeyMetricDialog();
	final WxbiVisualizationElement visualizationElement = keyMetricDialog.getVisualizationElement();
	visualizationElement.unpin();
	keyMetricDialog.cancel();

	final WxbiPage page = getCurrentPage();

//	// Wait for an alert to appear indicating that the unpining was successful or not.
//	final IbmAlertElement alertElement = page.getAlertElement(true /*fail*/);
//	// Make sure the alert indicates that the unpining was successful.
//	if(alertElement != null) {
//		if(alertElement.getStatus() != Success) throw new WaitElementTimeoutError("Unpinning key metric '" + name + "' failed with the following message: " + alertElement.getAlert());
//		// Dismiss the alert.
//		alertElement.close();
//	}

	// Check if the unpinned key metric is no longer in the carousel.
	final int timeout = page.timeout();
	final long timeoutMillis = timeout * 1000 + System.currentTimeMillis();
	while (getKeyMetricElement(name, false /*fail*/, user) != null) {
		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("The unpinned key metric with the name '" + name + "' remained open in the carousel before the timeout '" + timeout + "'s had reached.");
		}
		// Workaround for an unpinned key metric not getting removed until the page is refreshed.
		page.refresh();
	}
}
}
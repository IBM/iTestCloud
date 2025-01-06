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
package itest.cloud.ibm.page.element.wxbi.conversation;

import static itest.cloud.ibm.page.element.wxbi.metric.WxbiKeyMetricVisualizationElement.VISUALIZATION_ELEMENT_LOCATOR;
import static itest.cloud.page.Page.NO_DATA;
import static itest.cloud.scenario.ScenarioUtil.println;
import static org.openqa.selenium.Keys.ENTER;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.entity.wxbi.conversation.WxbiQuestionAnswer;
import itest.cloud.ibm.entity.wxbi.conversation.WxbiVisualizationType;
import itest.cloud.ibm.page.dialog.wxbi.WxbiKeyMetricDialog;
import itest.cloud.ibm.page.element.IbmElementWrapper;
import itest.cloud.ibm.page.element.wxbi.WxbiVisualizationElement;
import itest.cloud.ibm.page.element.wxbi.metric.*;
import itest.cloud.ibm.page.wxbia.WxbiHomePage;
import itest.cloud.ibm.scenario.error.IncorrectAnswerError;
import itest.cloud.ibm.scenario.error.InvalidQuestionError;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.scenario.error.WaitElementTimeoutError;

/**
 * This class defines and manages the editor element of a conversation.
 * <p>
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #getConversationName()}: Return the name of the conversation.</li>
 * <li>{@link #getKeyMetricVisualizationElement(boolean)}: Returns the visualization element of a key metric referred in the conversation.</li>
 * <li>{@link #getMessageElementWithVisualization(String)}: Return the message element containing a visualization element with a given name in the conversation.</li>
 * <li>{@link #getQuestionAnswer(String, boolean, boolean)}: Return information about a specific question in the conversation.</li>
 * <li>{@link #getMessageElements()}: Return the message elements in the conversation.</li>
 * <li>{@link #submitQuestion(String, boolean, String, boolean, WxbiVisualizationType, WxbiVisualizationType, boolean, boolean)}: Submit a given question in the conversation.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * <li>{@link #getExpectedTitle()}: Return a pattern matching the expected title for the current element.</li>
 * <li>{@link #getTitleElementLocator()}: Return the locator for the title element of the current element.</li>
 * </ul>
 * </p>
 */
public class WxbiConversationEditorElement extends IbmElementWrapper {

	private static final String ADDITIONAL_INFORMATION_YOU_MAY_FIND_USEFUL = "additional information you may find useful";
	private static final String NEW_CONVERSATION_MESSAGE_INDICATOR = "new conversation";
	private static final String CANCELLATION_MESSAGE = "Request cancelled";
	private static final By STOP_PROCESSING_ANSWER_ELEMENT_LOCATOR = By.xpath(".//*[@id='ripasso-conversation-stop']");
	private static final By TITLE_ELEMENT_LOCATOR = By.xpath(".//*[contains(@class,'title-value')]");
	private static final String[] ERROR_RESPONSES = new String[]
		{"different question", "rephrasing the question", "suggested question", "another question", "internal error",
		 "technical issue", "can't answer your question", "can't find data"};

public WxbiConversationEditorElement(final Page page) {
	this(page, NO_DATA);
	this.data = new String[] { waitForElement(TITLE_ELEMENT_LOCATOR).getText() };
}

public WxbiConversationEditorElement(final Page page, final String... data) {
	super(page, By.xpath("//*[contains(@class,'ripasso-main-view-content-chat')]"), data);
}

/**
 * Return the name of the conversation.
 *
 * @return The name of the conversation as {@link String}.
 */
public String getConversationName() {
	return this.data[0];
}

@Override
protected Pattern getExpectedTitle() {
	return Pattern.compile(Pattern.quote(this.data[0]));
}

/**
 * Returns the visualization element of a key metric referred in the conversation.
 *
 * @param fail Specifies whether to fail if a matching visualization element could not be found.
 *
 * @return The visualization element of a key metric referred in the conversation as {@link WxbiKeyMetricVisualizationElement} or
 * <code>null</code> if a matching visualization element could not be found and specified to fail in such a situation.
 */
public WxbiKeyMetricVisualizationElement getKeyMetricVisualizationElement(final boolean fail) {
	BrowserElement visualizationWebElement = waitForElement(VISUALIZATION_ELEMENT_LOCATOR, fail ? timeout() : tinyTimeout(), fail);

	return (visualizationWebElement != null) ? new WxbiKeyMetricVisualizationElement(this, visualizationWebElement) : null;
}

/**
 * Return the message elements in the conversation.
 *
 * @return The message elements in the conversation as a {@link List} of {@link WxbiMessageElement}.
 */
private List<WxbiMessageElement> getMessageElements() {
	return getMessageElements(null /*question*/, null /*visualization*/, false /*IsVisualizationInAnswer*/, false /*isAdditionalInformationAboutVisualizationInAnswer*/);
}

/**
 * Return the message elements in the conversation.
 *
 * @param question The question that the message elements are related to as {@link String}.
 * Provide <code>null</code> as the value of this parameter, if the message elements related to the given visualization or all the message elements in the conversation should be retrieved.
 * @param visualization The name of the visualization that the message elements are related to as {@link String}.
 * Provide <code>null</code> as the value of this parameter, if the message elements related to the given question or all the message elements in the conversation should be retrieved.
 * @param isVisualizationInAnswer Specifies whether a visualization is associated with the answer of the question if one is provided.
 * @param isAdditionalInformationAboutVisualizationInAnswer Specifies whether additional information about a visualization is expected to appear in the answer.
 *
 * @return The message elements in the conversation as a {@link List} of {@link WxbiMessageElement}.
 */
private List<WxbiMessageElement> getMessageElements(final String question, final String visualization, final boolean isVisualizationInAnswer, final boolean isAdditionalInformationAboutVisualizationInAnswer) {
	final List<BrowserElement> messageWebElements =
			waitForElements(By.xpath(".//*[contains(@class,'ripasso-message-list-view-container')]"), tinyTimeout(), false /*fail*/);
	final List<WxbiMessageElement> messageElements = new ArrayList<WxbiMessageElement>();

	for (int i = 0; i < messageWebElements.size(); i++) {
		final BrowserElement messageWebElement = messageWebElements.get(i);
		final WxbiMessageElement messageElement = new WxbiMessageElement(this, messageWebElement);
		final String messageText = messageElement.getMessage();
		final String messageWebElementText = messageWebElement.getText();

		// If a question is specified, only gather the message elements related to the particular question.
		if(question != null) {
			if((messageText != null) && messageText.contains(question)) {
				// Add the message element representing the question to the list.
				messageElements.add(messageElement);

				if(!isVisualizationInAnswer) {
					// If a visualization is not expected in the answer, then the next message element must contain the answer.
					// Therefore, simply return add it to the list and return the list.
					messageElements.add(new WxbiMessageElement(this, messageWebElements.get(i+1)));
					return messageElements;
				}

				// If a visualization is expected in the answer, consider the following scenarios.
				for (int j = i + 1; j < messageWebElements.size(); j++) {
					final WxbiMessageElement answerElement = new WxbiMessageElement(this, messageWebElements.get(j));
					messageElements.add(answerElement);

					// 1. A message element with the visualization following by another message element with additional information can be provided in the answer.
					// In this scenario, wait for the message element with additional information, which concludes the answer of the question.
					if(isAdditionalInformationAboutVisualizationInAnswer) {
						final String answerText = answerElement.getMessage();
						if((answerText != null) && answerText.contains(ADDITIONAL_INFORMATION_YOU_MAY_FIND_USEFUL)) return messageElements;
					// 2. A visualization alone can be provided in a message element.
					// In this scenario, wait for the particular message element with the visualization, which also concludes the answer of the question.
					}
					else if(answerElement.getVisualizationElement(false /*fail*/) != null) {
						return messageElements;
					}
				}
			}
		}
		// If a visualization is specified, only gather the message elements related to the particular visualization.
		else if(visualization != null) {
			final WxbiVisualizationElement visualizationElement = messageElement.getVisualizationElement(false /*fail*/);
			if((visualizationElement != null) && visualizationElement.getName().equals(visualization)) messageElements.add(messageElement);
		}
		// If a question is not specified, gather all the message elements.
		// Do not consider the welcome message as a desired one though.
		else if(!messageWebElementText.contains(NEW_CONVERSATION_MESSAGE_INDICATOR)) {
			messageElements.add(messageElement);
		}
	}

	return messageElements;
}

/**
 * Return the message element containing a visualization element with a given name in the conversation.
 *
 * @param visualization The name of the visualization as {@link String}.
 *
 * @return The message element containing a visualization element with the given name in the conversation as {@link WxbiMessageElement}.
 */
public WxbiMessageElement getMessageElementWithVisualization(final String visualization) {
	final List<WxbiMessageElement> messageElements =
		getMessageElements(null /*question*/, visualization, false /*isVisualizationInAnswer*/, false /*isAdditionalInformationAboutVisualizationInAnswer*/);
	return messageElements.get(messageElements.size() - 1);
}

@Override
protected WxbiHomePage getPage() {
	return (WxbiHomePage) super.getPage();
}

/**
 * Return information about a specific question in the conversation.
 *
 * @param question The question in the conversation as {@link String}.
 * @param isVisualizationInAnswer Specifies whether a visualization is associated with the answer of the question.
 * @param isAdditionalInformationAboutVisualizationInAnswer Specifies whether additional information about a visualization is expected to appear in the answer.
 *
 * @return The information about the question in the conversation as {@link WxbiQuestionAnswer}.
 */
public WxbiQuestionAnswer getQuestionAnswer(final String question, final boolean isVisualizationInAnswer, final boolean isAdditionalInformationAboutVisualizationInAnswer) {
	return new WxbiQuestionAnswer(getMessageElements(question, null /*visualization*/, isVisualizationInAnswer, isAdditionalInformationAboutVisualizationInAnswer));
}

@Override
protected By getTitleElementLocator() {
	return TITLE_ELEMENT_LOCATOR;
}

///**
// * Submit a question in the conversation.
// *
// * @param question The question to submit as {@link String}.
// * @param stopProcessingAnswer Specifies whether to stop processing the answer while an answer is being produced.
// *
// * @return The question scope object as {@link WxbiQuestionAnswer} containing references to the associated
// * question and answer elements as {@link WxbiMessageElement}.
// * @throws InvalidQuestionError If the submitted question is invalid.
// */
//public WxbiQuestionAnswer submitQuestion(final String question, final boolean stopProcessingAnswer) {
//	return submitQuestion(question, null /*expectedVisualizationTypeInQuestion*/, false /*suggestedQuestion*/, null /*expectedVisualizationTypeInAnswer*/, stopProcessingAnswer);
//}
//
///**
// * Submit a given question in the conversation.
// *
// * @param question The question to submit as {@link String}.
// * @param expectedVisualizationTypeInQuestion The type of visualization is expected to appear in the question.
// * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the question.
// * @param expectedVisualizationTypeInAnswer The type of visualization is expected to appear in the answer.
// * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the answer.
// *
// * @return The question scope object as {@link WxbiQuestionAnswer} containing references to the associated
// * question and answer elements as {@link WxbiMessageElement}.
// * @throws InvalidQuestionError If the submitted question is invalid.
// */
//public WxbiQuestionAnswer submitQuestion(final String question, final WxbiVisualizationType expectedVisualizationTypeInQuestion, final WxbiVisualizationType expectedVisualizationTypeInAnswer) {
//	return submitQuestion(question, expectedVisualizationTypeInQuestion, false /*suggestedQuestion*/, expectedVisualizationTypeInAnswer, false /*stopProcessingAnswer*/);
//}

private BrowserElement getStopProcessingAnswerElement(final boolean fail) {
	return waitForElement(STOP_PROCESSING_ANSWER_ELEMENT_LOCATOR, fail ? timeout() : tinyTimeout(), fail);
}

private boolean isAnswerBeingProcessed() {
	return getStopProcessingAnswerElement(false /*fail*/) != null;
}

private void stopProcessingAnswer() {
	getStopProcessingAnswerElement(true /*fail*/).click();
	this.browser.waitWhileDisplayed(this.element, STOP_PROCESSING_ANSWER_ELEMENT_LOCATOR, timeout(), true /*fail*/);
}

/**
 * Submit a question in the conversation.
 *
 * @param question The question to submit as {@link String}.
 * @param isSuggestedQuestion Specifies whether the given question should be submitted via the suggested questions.
 * @param expectedVisualizationTypeInQuestion The type of visualization is expected to appear in the question as {@link WxbiVisualizationType}.
 * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the question.
 * @param expectedVisualizationTypeInAnswer The type of visualization is expected to appear in the answer as {@link WxbiVisualizationType}.
 * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the answer.
 * @param isAdditionalInformationAboutVisualizationInAnswer Specifies whether additional information about a visualization is expected to appear in the answer.
 * @param stopProcessingAnswer Specifies whether to stop processing the answer while an answer is being produced.
 *
 * @return The question scope object as {@link WxbiQuestionAnswer} containing references to the associated
 * question and answer elements as {@link WxbiMessageElement}.
 * @throws InvalidQuestionError If the submitted question is invalid.
 */
public WxbiQuestionAnswer submitQuestion(final String question, final boolean isSuggestedQuestion, final String keyMetric, final boolean removeVisualizationFromQuestion, final WxbiVisualizationType expectedVisualizationTypeInQuestion, final WxbiVisualizationType expectedVisualizationTypeInAnswer, final boolean isAdditionalInformationAboutVisualizationInAnswer, final boolean stopProcessingAnswer) {
	// If an answer is currently being processed, stop the particular action.
	// A new question is not allowed while the answer of another is being processed.
	if(isAnswerBeingProcessed()) {
		stopProcessingAnswer();
	}

	// Record the existing message elements before submitting the question.
	List<WxbiMessageElement> existingMessageElements = getMessageElements();

	// If a key metric is specified, take the following actions.
	if(keyMetric != null) {
		// Open the appropriate key metric dialog.
		final WxbiCarouselElement carouselElement = getPage().openCarouselElement();
		final WxbiKeyMetricElement keyMetricElement = carouselElement.getKeyMetricElement(keyMetric, true /*fail*/);
		final WxbiKeyMetricDialog keyMetricDialog = keyMetricElement.openKeyMetricDialog();
		// Open the desired visualization type in the key metric dialog if one specified.
		if(expectedVisualizationTypeInQuestion != null) {
			final WxbiVisualizationElement visualizationElement = keyMetricDialog.getVisualizationElement();
			visualizationElement.openVisualization(expectedVisualizationTypeInQuestion);
		}
		// If the question is to be submitted as a suggested one and a key metric is specified for this purpose,
		// then do so via the key metric dialog. Otherwise, simply add the visualization of the key metric to the input field.
		// Either way, the visualization of the key metric will be referenced in the question to submit.
		if (isSuggestedQuestion) {
			keyMetricDialog.askQuestion(question);
		}
		else {
			keyMetricDialog.askQuestion();

			// Validate the existence of the above mentioned visualization element in the conversation editor element.
			final WxbiKeyMetricVisualizationElement keyMetricVisualizationElement = getKeyMetricVisualizationElement(true /*true*/);

			// Remove the referenced visualization of the key metric from the question to be submitted if asked to do so.
			if(removeVisualizationFromQuestion) {
				// Remove the reference of the visualization from the question to submit.
				keyMetricVisualizationElement.remove();
				// Check if the referenced visualization element is removed from the question to submit.
				long timeoutMillis = this.page.timeout() * 1000 + System.currentTimeMillis();
				while (getKeyMetricVisualizationElement(false /*true*/) != null) {
					if (System.currentTimeMillis() > timeoutMillis) {
						throw new WaitElementTimeoutError("The removed/detached visualization element of key metric '" + keyMetric + "' remained in the Conversation Editor before the timeout '" + this.page.timeout() + "'s had reached.");
					}
				}
			}

			// Submit the question as a custom one.
			submitCustomQuestion(question);
		}

	}
	// If a key metric is not specified, yet the the question is to be submitted as a suggested one,
	// then do so via the suggested questions in the conversation editor.
	else if(isSuggestedQuestion) {
		// Submit the suggested question.
		clickButton(By.xpath(".//button[contains(@class,'suggestion-question') and (text()='" + question + "')]"));
	}
	// If the question is supposed to be a custom one, submit it as such.
	else {
		submitCustomQuestion(question);
	}

	// Wait for the question to appear in the conversation editor.
	final WxbiMessageElement questionElement =
		waitForMessages(existingMessageElements, false /*visualizationInAnswer*/, false /*isAdditionalInformationInAnswer*/, timeout()).get(0 /*index*/);
	final String sumittedQuestion = questionElement.getMessage();
	// Validate the question.
	if(!sumittedQuestion.equalsIgnoreCase(question)) throw new InvalidQuestionError("The question submitted to the conversation editor '" + sumittedQuestion + "' was different from the question asked '" + question + "'.");

	// If a visualization is expected in the question,
	// ensure that the desired visualization element and its type are available in the question element.
	// Wait for an answer to appear in the conversation editor.
	final boolean isVisualizationInQuestion = (keyMetric != null) && !removeVisualizationFromQuestion;
	if(isVisualizationInQuestion) {
		final WxbiVisualizationElement visualizationElement = questionElement.getVisualizationElement(true /*fail*/);
		final WxbiVisualizationType actualVisualizationTypeInQuestion = visualizationElement.getVisualizationType();
		if((expectedVisualizationTypeInQuestion != null) && !expectedVisualizationTypeInQuestion.equals(actualVisualizationTypeInQuestion)) {
			throw new IncorrectAnswerError(question, expectedVisualizationTypeInQuestion, actualVisualizationTypeInQuestion, false /*isVisualizationInAnswer*/);
		}
	}

	// Record the existing message elements before waiting for the answer.
	existingMessageElements = getMessageElements();

	// If specified, stop processing the answer while an answer is being produced.
	if(stopProcessingAnswer) {
		stopProcessingAnswer();
	}

	// Wait for an answer to appear in the conversation editor.
	final boolean isVisualizationInAnswer = (expectedVisualizationTypeInAnswer != null) && !stopProcessingAnswer;
	final List<WxbiMessageElement> answerElements =
		waitForMessages(existingMessageElements, isVisualizationInAnswer, isAdditionalInformationAboutVisualizationInAnswer, timeout());
	final List<WxbiMessageElement> messageElements = new ArrayList<>(answerElements.size() + 1);
	messageElements.add(questionElement);
	messageElements.addAll(answerElements);
	final WxbiQuestionAnswer questionAnswer = new WxbiQuestionAnswer(messageElements);

	// Open the context menu of the question element if it is available.
	if(questionElement.isContextMenuAvailable()) {
		// Print the context info in the console after obtaining an answer.
		// This guarantees the final context as the context info gets updated whole the question is being analyzed.
		final WxbiQuestionContextMenuElement contextMenuElement = questionElement.getContextMenuElement();
		final String context = contextMenuElement.getContext();
		println("	  -> The following context information was provided for the question: " + context);
		// Close the context menu.
		contextMenuElement.collapse();
	}

	final WxbiMessageElement answerElement = questionAnswer.getAnswerElement();
	final String answer = (answerElement != null) ? answerElement.getMessage() : null;

	// Validate the answer in the following manner.
	// 1. If processing an answer was stopped, validate if a proper cancellation message is displayed in the answer element.
	if(stopProcessingAnswer) {
		if((answer == null) || !answer.contains(CANCELLATION_MESSAGE)) throw new IncorrectAnswerError("A cancellation message such as '" + CANCELLATION_MESSAGE.toLowerCase() + "' was expected to appear in the conversation editor, but the following was displayed instead: '" + answer);
	}
	// 2. If an answer was provided, make sure the provided answer is NOT an error response.
	else if(answer != null) {
		for (String errorResponse : ERROR_RESPONSES) {
			if(answer.contains(errorResponse)) throw new IncorrectAnswerError("The error response '" + answer + "' was provided as the answer for the question '" + question + "'.");
		}
	}

	// If a visualization is expected in the answer,
	// ensure that the desired visualization element and its type are available in the answer.
	if(isVisualizationInAnswer) {
		final WxbiVisualizationElement visualizationElement = questionAnswer.getChartElement().getVisualizationElement(true /*fail*/);
		final WxbiVisualizationType actualVisualizationTypeInAnswer = visualizationElement.getVisualizationType();
		if(!actualVisualizationTypeInAnswer.equals(expectedVisualizationTypeInAnswer)) {
			throw new IncorrectAnswerError(question, expectedVisualizationTypeInAnswer, actualVisualizationTypeInAnswer, true /*isVisualizationInAnswer*/);
		}
	}

	return questionAnswer;
}

private void submitCustomQuestion(final String question) {
	typeText(By.xpath(".//*[@id='ripasso-input']"), question, ENTER);
}

///**
// * Submit a given suggested question in the conversation.
// *
// * @param question The question to submit as {@link String}.
// * @param expectedVisualizationTypeInQuestion The type of visualization is expected to appear in the question.
// * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the question.
// * @param expectedVisualizationTypeInAnswer The type of visualization is expected to appear in the answer.
// * <code>null</code> should be provided as the value of this parameter if a visualization is not expected in the answer.
// *
// * @return The question scope object as {@link WxbiQuestionAnswer} containing references to the associated
// * question and answer elements as {@link WxbiMessageElement}.
// * @throws InvalidQuestionError If the submitted question is invalid.
// */
//public WxbiQuestionAnswer submitSuggestedQuestion(final WxbiSuggestedQuestion question, final WxbiVisualizationType expectedVisualizationTypeInQuestion, final WxbiVisualizationType expectedVisualizationTypeInAnswer) {
//	return submitQuestion(question.label, expectedVisualizationTypeInQuestion, true /*suggestedQuestion*/, expectedVisualizationTypeInAnswer, false /*stopProcessingAnswer*/);
//}

private List<WxbiMessageElement> waitForMessages(final List<WxbiMessageElement> existingMessageElements, final boolean isVisualizationInAnswer, final boolean isAdditionalInformationAboutVisualizationInAnswer, final int timeout) {
	// Wait for new messages.
	final int existingMessageCount = existingMessageElements.size();
	List<WxbiMessageElement> currentMessages;
	final long timeoutMillis = timeout * 1000 + System.currentTimeMillis();

	while (true) {
		// Wait for new messages to appear in the editor element.
		if((currentMessages = getMessageElements()).size() > existingMessageCount) {
			// If reached here, it implies that at least one new message has been added in the editor element.
			if(!isVisualizationInAnswer) {
				// If a visualization is not expected in the answer, then the newly added message element must contain the answer.
				// Therefore, simply return it.
				break;
			}

			final WxbiMessageElement lastMessageElement = currentMessages.get(currentMessages.size() - 1);
			// If a visualization is expected in the answer, consider the following scenarios.
			// 1. A message element with the visualization following by another message element with additional information can be provided in the answer.
			// In this scenario, wait for the message element with additional information, which concludes the answer of the question.
			if(isAdditionalInformationAboutVisualizationInAnswer) {
				final String lastMessageText = lastMessageElement.getMessage();
				if((lastMessageText != null) && lastMessageText.contains(ADDITIONAL_INFORMATION_YOU_MAY_FIND_USEFUL)) break;
			}
			// 2. A visualization alone can be provided in a message element.
			// In this scenario, wait for the particular message element with the visualization, which also concludes the answer of the question.
			else if(lastMessageElement.getVisualizationElement(false /*fail*/) != null) break;
		}

		if (System.currentTimeMillis() > timeoutMillis) {
			throw new WaitElementTimeoutError("A new message(s) did not appear in the conversation editor before the '" + timeout + "' seconds has reached");
		}
	}

	// Return the new messages.
	return currentMessages.subList(existingMessageCount, currentMessages.size());
}
}
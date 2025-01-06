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
package itest.cloud.ibm.entity.wxbi.conversation;

import java.util.List;

import itest.cloud.ibm.page.element.wxbi.conversation.WxbiMessageElement;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This class contains information about a question in a conversation. Such information includes the question,
 * the answer, and the context that was automatically selected for the question.
 * <p>
 * </p>
 * Following public features are accessible from this class:
 * <ul>
 * <li>{@link #getAdditionalInformationElement()}: Returns the element containing additional information.</li>
 * <li>{@link #getAnswerElement()}: Returns the element containing the answer.</li>
 * <li>{@link #getChartElement()}: Returns the element containing the chart.</li>
 * <li>{@link #getContext()}: Returns the context of the question.</li>
 * <li>{@link #getQuestionElement()}: Returns the element containing the question.</li>
 * </ul>
 * </p><p>
 * Following internal features are overridden in this class:
 * <ul>
 * </ul>
 * </p>
 */
public class WxbiQuestionAnswer {

	private final WxbiMessageElement questionElement, answerElement, chartElement, additionalInformationElement;
	private final String context;

public WxbiQuestionAnswer(final List<WxbiMessageElement> messageElements) {
	final int messageElementCount = messageElements.size();

	if(messageElementCount == 4) {
		this.answerElement = messageElements.get(1 /*index*/);
		this.chartElement = messageElements.get(2 /*index*/);
		this.additionalInformationElement = messageElements.get(3 /*index*/);
	}
	else if(messageElementCount == 3) {
		final WxbiMessageElement lastElement = messageElements.get(messageElements.size() -1 /*index*/);

		if(lastElement.getVisualizationElement(false /*fail*/) != null) {
			this.answerElement = messageElements.get(1 /*index*/);;
			this.chartElement = lastElement;
			this.additionalInformationElement = null;
		}
		else {
			this.answerElement = null;
			this.chartElement = messageElements.get(1 /*index*/);
			this.additionalInformationElement = lastElement;
		}
	}
	else if(messageElementCount == 2) {
		final WxbiMessageElement lastElement = messageElements.get(messageElements.size() -1 /*index*/);

		if(lastElement.getVisualizationElement(false /*fail*/) != null) {
			this.answerElement = null;
			this.chartElement = lastElement;
			this.additionalInformationElement = null;
		}
		else {
			this.answerElement = lastElement;
			this.chartElement = null;
			this.additionalInformationElement = null;
		}
	}
	else {
		throw new ScenarioFailedError("More or less message elements (" + messageElementCount + ") than what this method can handle (" + 4 + ") were provided.");
	}
	this.questionElement = messageElements.get(0 /*index*/);
	this.context = this.questionElement.isContextMenuAvailable() ? this.questionElement.getContextMenuElement().getContext() : null;
}

/**
 * Returns the element containing additional information.
 *
 * @return The element containing additional information as {@link WxbiMessageElement}.
 */
public WxbiMessageElement getAdditionalInformationElement() {
	return this.additionalInformationElement;
}

/**
 * Returns the element containing the answer.
 *
 * @return The element containing the answer as {@link WxbiMessageElement}.
 */
public WxbiMessageElement getAnswerElement() {
	return this.answerElement;
}

/**
 * Returns the element containing the chart.
 *
 * @return The element containing the chart as {@link WxbiMessageElement}.
 */
public WxbiMessageElement getChartElement() {
	return this.chartElement;
}

/**
 * Returns the context of the question.
 *
 * @return The context of the question as {@link String}.
 */
public String getContext() {
	return this.context;
}

/**
 * Returns the element containing the question.
 *
 * @return The element containing the question as {@link WxbiMessageElement}.
 */
public WxbiMessageElement getQuestionElement() {
	return this.questionElement;
}
}
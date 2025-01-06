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
package itest.cloud.ibm.test.scenario.wxbi;

import static itest.cloud.ibm.entity.wxbi.conversation.WxbiVisualizationType.getType;

import java.util.List;

import org.junit.runner.RunWith;

import itest.cloud.ibm.entity.wxbi.conversation.WxbiQuestionAnswer;
import itest.cloud.ibm.entity.wxbi.conversation.WxbiVisualizationType;
import itest.cloud.ibm.page.dialog.wxbi.WxbiKeyMetricDialog;
import itest.cloud.ibm.page.element.wxbi.WxbiVisualizationElement;
import itest.cloud.ibm.page.element.wxbi.conversation.WxbiMessageElement;
import itest.cloud.ibm.scenario.error.IncorrectAnswerError;
import itest.cloud.ibm.scenario.error.IncorrectContextError;
import itest.cloud.ibm.scenario.wxbi.WxbiScenarioStep;
import itest.cloud.ibm.test.scenario.IbmTestScenarioData;
import itest.cloud.ibm.test.scenario.IbmTestScenarioExecution;
import itest.cloud.scenario.ScenarioStepRunner;

/**
 * Manage common functionalities of the WXBI scenario steps.
 */
@RunWith(ScenarioStepRunner.class)
public class WxbiTestScenarioStep extends WxbiScenarioStep implements WxbiTestScenarioConstants {

protected void switchThroughVisualizationsOfKeyMetric(final String keyMetric) {
	final WxbiKeyMetricDialog dialog = openKeyMetric(keyMetric, getData().getTestUser());
	final WxbiVisualizationElement visualizationElement = dialog.getVisualizationElement();
	final List<String> supportedVisualizationTypes = visualizationElement.getSupportedVisualizationTypes();

	for (String visualizationType : supportedVisualizationTypes) {
		visualizationElement.openVisualization(getType(visualizationType));
	}

	dialog.cancel();
}

protected void askSuggestedQuestion(final String keyMetric, final int suggestedQuestionIndex) {
	final WxbiKeyMetricDialog dialog = openKeyMetric(keyMetric, getData().getTestUser());
	final List<String> suggestedQuestions = dialog.getSuggestedQuestions();
	final WxbiVisualizationElement visualizationElement = dialog.getVisualizationElement();
	final List<String> supportedVisualizationTypes = visualizationElement.getSupportedVisualizationTypes();

	submitSuggestedQuestion(getData().getConversationName(), suggestedQuestions.get(suggestedQuestionIndex), keyMetric,
		getType(supportedVisualizationTypes.get(suggestedQuestionIndex % supportedVisualizationTypes.size())),
		null /*expectedVisualizationTypeInAnswer*/, getData().getTestUser());
}

protected WxbiQuestionAnswer submitQuestion(final String question, final String... expectedAnswers) {
	return submitQuestion(question, false /*isSuggestedQuestion*/, null /*keyMetric*/, null /*expectedVisualizationTypeInAnswer*/, null /*expectedVisualizationTypeInAnswer*/, false /*isAdditionalInformationAboutVisualizationInAnswer*/, expectedAnswers);
}

protected WxbiQuestionAnswer submitQuestion(final String question, final WxbiVisualizationType expectedVisualizationTypeInAnswer, final String... expectedAnswers) {
	return submitQuestion(question, false /*isSuggestedQuestion*/, null /*keyMetric*/, null /*expectedVisualizationTypeInQuestion*/, expectedVisualizationTypeInAnswer, true /*isAdditionalInformationAboutVisualizationInAnswer*/, expectedAnswers);
}

protected WxbiQuestionAnswer submitQuestion(final String question, final WxbiVisualizationType expectedVisualizationTypeInAnswer, final boolean isAdditionalInformationAboutVisualizationInAnswer, final String... expectedAnswers) {
	return submitQuestion(question, false /*isSuggestedQuestion*/, null /*keyMetric*/, null /*expectedVisualizationTypeInQuestion*/, expectedVisualizationTypeInAnswer, isAdditionalInformationAboutVisualizationInAnswer, expectedAnswers);
}

protected WxbiQuestionAnswer submitQuestion(final String question, final String keyMetric, final WxbiVisualizationType expectedVisualizationTypeInQuestion, final WxbiVisualizationType expectedVisualizationTypeInAnswer, final String... expectedAnswers) {
	return submitQuestion(question, false /*isSuggestedQuestion*/, keyMetric, expectedVisualizationTypeInQuestion, expectedVisualizationTypeInAnswer, true /*isAdditionalInformationAboutVisualizationInAnswer*/, expectedAnswers);
}

protected WxbiQuestionAnswer submitQuestion(final String question, final boolean isSuggestedQuestion, final String keyMetric, final WxbiVisualizationType expectedVisualizationTypeInQuestion, final WxbiVisualizationType expectedVisualizationTypeInAnswer, final boolean isAdditionalInformationAboutVisualizationInAnswer, final String... expectedAnswers) {
	// Submit the question.
	final WxbiQuestionAnswer questionAnswer =
		submitQuestion(getData().getConversationName(), question, isSuggestedQuestion, keyMetric, false /*removeVisualizationFromQuestion*/, expectedVisualizationTypeInQuestion, expectedVisualizationTypeInAnswer, isAdditionalInformationAboutVisualizationInAnswer, false /*stopProcessingAnswer*/, getData().getTestUser());

	// Validate the context of the question.
	final String context = questionAnswer.getContext();
	if(context != null) {
		for (String dataSource : DATA_SOURCES) {
			if(!context.contains(dataSource)) {
				throw new IncorrectContextError(question, dataSource, context);
			}
		}

		for (String erroneousKeyword : ERRONEOUS_KEYWORDS_IN_CONTEXT) {
			if(context.contains(erroneousKeyword)) {
				throw new IncorrectContextError("A text such as '" + erroneousKeyword + "' is not expected to be in the context information.");
			}
		}
	}

	// Validate the answer if required.
	if((expectedAnswers != null) && (expectedAnswers.length > 0)) {
		final WxbiMessageElement answerElement = questionAnswer.getAnswerElement();
		final String answer = answerElement.getMessage();

		for (String expectedAnswer : expectedAnswers) {
			if(!answer.contains(expectedAnswer)) {
				throw new IncorrectAnswerError(question, expectedAnswer, answer);
			}
		}
	}

	return questionAnswer;
}

protected WxbiQuestionAnswer submitSuggestedQuestion(final String question, final String... expectedAnswers) {
	return submitQuestion(question, true /*isSuggestedQuestion*/, null /*keyMetric*/, null /*expectedVisualizationTypeInAnswer*/, null /*expectedVisualizationTypeInAnswer*/, false /*isAdditionalInformationAboutVisualizationInAnswer*/, expectedAnswers);
}

/**
 * {@inheritDoc}
 *
 * @return The scenario execution as {@link IbmTestScenarioData}
 */
@Override
protected IbmTestScenarioData getData() {
	return (IbmTestScenarioData) super.getData();
}

/**
 * {@inheritDoc}
 *
 * @return The scenario execution as a {@link IbmTestScenarioExecution}.
 */
@Override
protected IbmTestScenarioExecution getScenarioExecution() {
	return (IbmTestScenarioExecution) super.getScenarioExecution();
}
}
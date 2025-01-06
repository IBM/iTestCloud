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
package itest.cloud.ibm.scenario.error;

import itest.cloud.ibm.entity.wxbi.conversation.WxbiVisualizationType;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This class represents an error raised due to an incorrect answer to a question in a conversation.
 */
public class IncorrectAnswerError extends ScenarioFailedError {

public IncorrectAnswerError(final String text) {
	super(text);
}

public IncorrectAnswerError(final String question, final String expectedAnswer, final String actualAnswer) {
	super("The answer to the question ('" + question + "') was expected to be '" + expectedAnswer + "', but given as '" + actualAnswer + "'.");
}

public IncorrectAnswerError(final String question, final WxbiVisualizationType expectedVisualizationType, final WxbiVisualizationType actualVisualizationType, final boolean isVisualizationInAnswer) {
	super((isVisualizationInAnswer ? "The answer to t" : "T") + "he question ('" + question + "') was expected to have a visualization of type '" + expectedVisualizationType.label + "', but a visualization of type '" + actualVisualizationType.label + "' was given instead.");
}
}
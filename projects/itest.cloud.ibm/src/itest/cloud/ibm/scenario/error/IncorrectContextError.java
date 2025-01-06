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

import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This class represents an error raised due to the use of an incorrect context to provide an answer to a question in a conversation.
 */
public class IncorrectContextError extends ScenarioFailedError {

public IncorrectContextError(final String text) {
	super(text);
}

public IncorrectContextError(final String question, final String expectedContext, final String actualContext) {
	super("The context of the question ('" + question + "') was expected to be '" + expectedContext + "', but given as '" + actualContext + "'.");
}
}
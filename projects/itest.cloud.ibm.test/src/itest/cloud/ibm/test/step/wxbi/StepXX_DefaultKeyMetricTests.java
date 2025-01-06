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

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import itest.cloud.annotation.Dependency;
import itest.cloud.ibm.test.scenario.IbmTestScenarioStepRunner;
import itest.cloud.ibm.test.scenario.wxbi.WxbiTestScenarioStep;

/**
 * This class defines a set of tests for validating various aspects of a default key metric.
 * <p>
 * The following is a list of tests associated with this test suite:
 * <ul>
 * <li>{@link #test01_SwitchThroughVisualizationsOfKeyMetric()}: Switch through the visualizations of key metric.</li>
 * <li>{@link #test02_CreateConversation()}: Create the conversation.</li>
 * <li>{@link #test03_SwitchVisualizationOfKeyMetricAndAskSuggestedQuestion1()}: Switch the visualization of the key metric and ask a suggested question 1.</li>
 * <li>{@link #test04_RecreateConversation()}: Recreate the conversation.</li>
 * <li>{@link #test05_SwitchVisualizationOfKeyMetricAndAskSuggestedQuestion2()}: Switch the visualization of the key metric and ask a suggested question 2.</li>
 * <li>{@link #test06_RecreateConversationForSecondTime()}: Recreate the conversation for the second time.</li>
 * <li>{@link #test07_SwitchVisualizationOfKeyMetricAndAskSuggestedQuestion3()}: Switch the visualization of the key metric and ask a suggested question 3.</li>
 * <li>{@link #test08_DeleteConversation()}: Delete the conversation.</li>
 * </ul>
 * </p>
 */
@RunWith(IbmTestScenarioStepRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public abstract class StepXX_DefaultKeyMetricTests extends WxbiTestScenarioStep {

	private static final String TEST_CREATE_CONVERSATION = CLASS_INDICATOR_OF_DEPENDENCY + ".test02_CreateConversation";
	private static final String TEST_RECREATE_CONVERSATION = CLASS_INDICATOR_OF_DEPENDENCY + ".test04_RecreateConversation";
	private static final String TEST_RECREATE_CONVERSATION_FOR_SECOND_TIME = CLASS_INDICATOR_OF_DEPENDENCY + ".test06_RecreateConversationForSecondTime";

/**
 * Return the name of the key metric.
 *
 * @return The name of the key metric as {@link String}.
 */
protected abstract String getKeyMetricName();

/**
 * Switch through the visualizations of key metric.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test01_SwitchThroughVisualizationsOfKeyMetric() {
	switchThroughVisualizationsOfKeyMetric(getKeyMetricName());
}

/**
 * Create the conversation.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test02_CreateConversation() {
	createConversation(getData().getConversationName(), true /*force*/, getData().getTestUser());
}

/**
 * Switch the visualization of the key metric and ask a suggested question 1.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test03_SwitchVisualizationOfKeyMetricAndAskSuggestedQuestion1() {
	askSuggestedQuestion(getKeyMetricName(), 0 /*suggestedQuestionIndex*/);
}

/**
 * Recreate the conversation.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test04_RecreateConversation() {
	createConversation(getData().getConversationName(), true /*force*/, getData().getTestUser());
}

/**
 * Switch the visualization of the key metric and ask a suggested question 2.
 */
@Test
@Dependency({TEST_RECREATE_CONVERSATION})
public void test05_SwitchVisualizationOfKeyMetricAndAskSuggestedQuestion2() {
	askSuggestedQuestion(getKeyMetricName(), 1 /*suggestedQuestionIndex*/);
}

/**
 * Recreate the conversation for the second time.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test06_RecreateConversationForSecondTime() {
	createConversation(getData().getConversationName(), true /*force*/, getData().getTestUser());
}

/**
 * Switch the visualization of the key metric and ask a suggested question 3.
 */
@Test
@Dependency({TEST_RECREATE_CONVERSATION_FOR_SECOND_TIME})
public void test07_SwitchVisualizationOfKeyMetricAndAskSuggestedQuestion3() {
	askSuggestedQuestion(getKeyMetricName(), 2 /*suggestedQuestionIndex*/);
}

/**
 * Delete the conversation.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test08_DeleteConversation() {
	deleteConversation(getData().getConversationName(), getData().getTestUser());
}
}
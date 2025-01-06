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
import itest.cloud.ibm.annotation.wxbi.CloudTest;
import itest.cloud.ibm.test.scenario.IbmTestScenarioStepRunner;
import itest.cloud.ibm.test.scenario.wxbi.WxbiTestScenarioStep;

/**
 * This class defines a set of tests to validate the free form tests and their associated functionality.
 * <p>
 * The following is a list of tests associated with this test suite:
 * <ul>
 * <li>{@link #test00_CreateConversation()}: Create the conversation.</li>
 * <li>{@link #test01_SubmitSuggestedQuestion()}: Submit a suggested question.</li>
 * <li>{@link #test02_SubmitQuestionByReferencingKeyMetricAndItsVisualization()}: Submit a free form question by referencing a key metric and attaching its visualization to the question.</li>
 * <li>{@link #test03_SubmitQuestionByReferencingKeyMetricButRemovingItsVisualization()}: Submit a free form question by referencing a key metric, but removing its visualization from the question.</li>
 * <li>{@link #test04_SubmitQuestionButStopProcessingAnswer()}: Submit a question, but stop processing the answer while an answer is being produced.</li>
 * <li>{@link #test05_DeleteConversation()}: Delete the conversation.</li>
 * </ul>
 * </p>
 */
@RunWith(IbmTestScenarioStepRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StepB02_BasicQuestionsTests extends WxbiTestScenarioStep {

	private static final String QUESTION_WHICH_COUNTRY_HAS_SECOND_HIGHEST_REVENUE = "Which country has the second highest revenue?";
	private static final String QUESTION_WHICH_PRODUCT_HAS_SECOND_BEST_SALES = "Which product has the second best sales?";

	private static final String KEY_METRIC_PRODUCT_REVENUE_SHARE = "Product revenue share";
	private static final String KEY_METRIC_TOP_10_PRODUCT_BRANDS_BY_REVENUE = "Top 10 product brands by revenue";

	private static final String TEST_CREATE_CONVERSATION = CLASS_INDICATOR_OF_DEPENDENCY + ".test00_CreateConversation";

/**
 * Create the conversation.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test00_CreateConversation() {
	createConversation(getData().getConversationName(), true /*force*/, getData().getTestUser());
}

/**
 * Submit a suggested question.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test01_SubmitSuggestedQuestion() {
	submitSuggestedQuestion(QUESTION_WHICH_REGION_HAS_MOST_SALES, ANSWER_WHICH_REGION_HAS_MOST_SALES);
}

/**
 * Submit a question by referencing a key metric and attaching its visualization to the question.
 */
@Test
@CloudTest
@Dependency({TEST_CREATE_CONVERSATION})
public void test02_SubmitQuestionByReferencingKeyMetricAndItsVisualization() {
	submitQuestion(getData().getConversationName(), QUESTION_WHICH_PRODUCT_HAS_SECOND_BEST_SALES, KEY_METRIC_TOP_10_PRODUCT_BRANDS_BY_REVENUE, false /*removeVisualizationFromQuestion*/, getData().getTestUser());
}

/**
 * Submit a question by referencing a key metric, but removing its visualization from the question.
 */
@Test
@CloudTest
@Dependency({TEST_CREATE_CONVERSATION})
public void test03_SubmitQuestionByReferencingKeyMetricButRemovingItsVisualization() {
	submitQuestion(getData().getConversationName(), QUESTION_WHICH_COUNTRY_HAS_SECOND_HIGHEST_REVENUE, KEY_METRIC_PRODUCT_REVENUE_SHARE, true /*removeVisualizationFromQuestion*/, getData().getTestUser());
}

/**
 * Submit a question, but stop processing the answer while an answer is being produced.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test04_SubmitQuestionButStopProcessingAnswer() {
	submitQuestion(getData().getConversationName(), QUESTION_WHICH_REGION_HAS_MOST_SALES, true /*stopProcessingAnswer*/, getData().getTestUser());
}

/**
 * Delete the conversation.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test05_DeleteConversation() {
	deleteConversation(getData().getConversationName(), getData().getTestUser());
}
}
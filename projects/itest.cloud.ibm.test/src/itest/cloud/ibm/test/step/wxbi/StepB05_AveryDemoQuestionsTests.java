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

import static itest.cloud.ibm.entity.wxbi.conversation.WxbiVisualizationType.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import itest.cloud.annotation.Dependency;
import itest.cloud.ibm.entity.wxbi.conversation.WxbiQuestionAnswer;
import itest.cloud.ibm.page.element.wxbi.WxbiVisualizationElement;
import itest.cloud.ibm.page.element.wxbi.conversation.WxbiMessageElement;
import itest.cloud.ibm.scenario.error.IncorrectAnswerError;
import itest.cloud.ibm.test.scenario.IbmTestScenarioStepRunner;
import itest.cloud.ibm.test.scenario.wxbi.WxbiTestScenarioStep;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This class defines a set of sanity tests.
 * <p>
 * The following is a list of tests associated with the functionality presented in a demo by Avery:
 * <ul>
 * <li>{@link #test00_CreateConversation()}: Create the conversation.</li>
 * <li>{@link #test01_QuestionWhichBrandContributesMostToTotalRevenueLastYear()}: Submit the question 'Which brand contributes the most to total revenue last year?'.</li>
 * <li>{@link #test02_QuestionWhatBrandsDoWeCarry()}: Submit the question 'What brands do we carry?'.</li>
 * <li>{@link #test03_RecreateConversation()}: Recreate the conversation.</li>
 * <li>{@link #test04_ShowRevenueByRegionInTable()}: Show revenue by region in a list chart'.</li>
 * <li>{@link #test05_UsePointChartInstead()}: Delete the conversation.</li>
 * <li>{@link #test06_ChangeTitleOfCart()}: Delete the conversation.</li>
 * <li>{@link #test07_ShowRevenueByRegionAsPackedBubbleChart()}: Delete the conversation.</li>
 * <li>{@link #test08_RecreateConversationForSecondTime()}: Delete the conversation.</li>
 * <li>{@link #test09_QuestionAreThereAnySeasonalTrendsInRevenueForAllRegionsBasedOnMonth()}: Delete the conversation.</li>
 * <li>{@link #test10_QuestionWhatWasMyCustomerChurnLastQuarter()}: Delete the conversation.</li>
 * <li>{@link #test11_RecreateConversationForThirdTime()}: Delete the conversation.</li>
 * <li>{@link #test12_QuestionWhatDrivesRevenue()}: Delete the conversation.</li>
 * <li>{@link #test13_DeleteConversation()}: Delete the conversation.</li>
 * </ul>
 * </p>
 */
@RunWith(IbmTestScenarioStepRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StepB05_AveryDemoQuestionsTests extends WxbiTestScenarioStep {

	private static final String REVENUE_ANALYSIS = "Revenue analysis";

	private static final String QUESTION_USING_PACKED_BUBBLE_CHART_SHOW_REVENUE_BY_REGION_WITH_TITLE_REGIONAL_REVENUE_ANALYSIS = "Using a Packed Bubble chart show revenue by region with title \"Regional Revenue Analysis\"";
	private static final String QUESTION_CHANGE_TITLE_TO_REVENUE_ANALYSIS = "Change the title to \"" + REVENUE_ANALYSIS + "\"";
	private static final String QUESTION_USE_POINT_CHART_INSTEAD = "Use a point chart instead";
	private static final String QUESTION_SHOW_REVENUE_BY_REGION_IN_LIST = "Show revenue by region in a list";
	private static final String QUESTION_WHAT_BRANDS_DO_WE_CARRY = "What brands do we carry?";
	private static final String QUESTION_WHAT_DRIVES_REVENUE = "What drives revenue?";

	private static final String QUESTION_WHICH_BRAND_CONTRIBUTES_MOST_TO_TOTAL_REVENUE_LAST_YEAR = "Which brand contributes the most to total revenue last year?";
	private static final String[] ANSWER_TO_QUESTION_WHICH_BRAND_CONTRIBUTES_MOST_TO_TOTAL_REVENUE_LAST_YEAR = new String[] {"Hailstorm", "$145,172,486"};

	private static final String TEST_CREATE_CONVERSATION = CLASS_INDICATOR_OF_DEPENDENCY + ".test00_CreateConversation";
	private static final String TEST_RECREATE_CONVERSATION = CLASS_INDICATOR_OF_DEPENDENCY + ".test03_RecreateConversation";
	private static final String TEST_SHOW_REVENUE_BY_REGION_IN_TABLE = CLASS_INDICATOR_OF_DEPENDENCY + ".test04_ShowRevenueByRegionInTable";
	private static final String TEST_USE_POINT_CHART_INSTEAD = CLASS_INDICATOR_OF_DEPENDENCY + ".test05_UsePointChartInstead";
	private static final String TEST_RECREATE_CONVERSATION_FOR_SECOND_TIME = CLASS_INDICATOR_OF_DEPENDENCY + ".test08_RecreateConversationForSecondTime";
	private static final String TEST_RECREATE_CONVERSATION_FOR_THIRD_TIME = CLASS_INDICATOR_OF_DEPENDENCY + ".test11_RecreateConversationForThirdTime";

/**
 * Create the conversation.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test00_CreateConversation() {
	createConversation(getData().getConversationName(), true /*force*/, getData().getTestUser());
}

/**
 * Submit the question 'Which brand contributes the most to total revenue last year?'.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test01_QuestionWhichBrandContributesMostToTotalRevenueLastYear() {
	submitQuestion(QUESTION_WHICH_BRAND_CONTRIBUTES_MOST_TO_TOTAL_REVENUE_LAST_YEAR, ANSWER_TO_QUESTION_WHICH_BRAND_CONTRIBUTES_MOST_TO_TOTAL_REVENUE_LAST_YEAR);
}

/**
 * Submit the question 'What brands do we carry?'.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test02_QuestionWhatBrandsDoWeCarry() {
	submitQuestion(QUESTION_WHAT_BRANDS_DO_WE_CARRY, LIST /*expectedVisualizationTypeInAnswer*/, false /*isAdditionalInformationAboutVisualizationInAnswer*/, ANSWER_DIFFICULT_TO_SUMMARIZE_CHART_CREATED);
	// Validate the content of the list chart
}

/**
 * Recreate the conversation.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test03_RecreateConversation() {
	createConversation(getData().getConversationName(), true /*force*/, getData().getTestUser());
}

/**
 * Show revenue by region in a list chart'.
 */
@Test
@Dependency({TEST_RECREATE_CONVERSATION})
public void test04_ShowRevenueByRegionInTable() {
	submitQuestion(QUESTION_SHOW_REVENUE_BY_REGION_IN_LIST, LIST /*expectedVisualizationTypeInAnswer*/);
	// Validate the content of the list chart
}

/**
 * Show revenue by region in a point chart'.
 */
@Test
@Dependency({TEST_SHOW_REVENUE_BY_REGION_IN_TABLE})
public void test05_UsePointChartInstead() {
	submitQuestion(QUESTION_USE_POINT_CHART_INSTEAD, POINT /*expectedVisualizationTypeInAnswer*/);
}

/**
 * Change the title of the 'revenue by region' chart to Change 'revenue analysis'.
 */
@Test
@Dependency({TEST_USE_POINT_CHART_INSTEAD})
public void test06_ChangeTitleOfCart() {
	final WxbiQuestionAnswer questionAnswer = submitQuestion(QUESTION_CHANGE_TITLE_TO_REVENUE_ANALYSIS, POINT /*expectedVisualizationTypeInAnswer*/);
	// Validate the title of the new chart.
	final WxbiMessageElement chartElement = questionAnswer.getChartElement();
	final WxbiVisualizationElement visualizationElement = chartElement.getVisualizationElement(true /*fail*/);
	final String chartTitle = visualizationElement.getTitle();
	if(!chartTitle.equals(REVENUE_ANALYSIS)) throw new ScenarioFailedError("The title of the chat was expected to be '" + REVENUE_ANALYSIS + "', but was given as '" + chartTitle + "' instead.");
}

/**
 * Show revenue by region in a bubble chart with a custom title.
 */
@Test
@Dependency({TEST_RECREATE_CONVERSATION})
public void test07_ShowRevenueByRegionAsPackedBubbleChart() {
	submitQuestion(QUESTION_USING_PACKED_BUBBLE_CHART_SHOW_REVENUE_BY_REGION_WITH_TITLE_REGIONAL_REVENUE_ANALYSIS, PACKED_BUBBLE /*expectedVisualizationTypeInAnswer*/);
}

/**
 * Recreate the conversation.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test08_RecreateConversationForSecondTime() {
	createConversation(getData().getConversationName(), true /*force*/, getData().getTestUser());
}

/**
 * Submit the question 'Are there any seasonal trends in revenue for all regions based on the month?'.
 */
@Test
@Dependency({TEST_RECREATE_CONVERSATION_FOR_SECOND_TIME})
public void test09_QuestionAreThereAnySeasonalTrendsInRevenueForAllRegionsBasedOnMonth() {
	submitQuestion(QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_FOR_ALL_REGIONS_BASED_ON_MONTH, COLUMN /*expectedVisualizationTypeInAnswer*/);
}

/**
 * Submit the question 'What was my customer churn last quarter?'.
 */
@Test
@Dependency({TEST_RECREATE_CONVERSATION_FOR_SECOND_TIME})
public void test10_QuestionWhatWasMyCustomerChurnLastQuarter() {
	try {
		final String question = QUESTION_WHAT_WAS_MY_CUSTOMER_CHURN_LAST_QUARTER;
		final WxbiQuestionAnswer questionAnswer = submitQuestion(question);
		// If reached here, it implies that the question was answered by Ripasso even though it should not have due to the question being out of scope.
		// Therefore, fail the test.
		throw new ScenarioFailedError("The following answer was provided for the question '" + question + "' rather than informing the user that the question was out of scope: " + questionAnswer.getAnswerElement().getMessage());
	}
	catch (IncorrectAnswerError e) {
		// If reached here, it implies that Ripasso could not answer the particular question due to it is being out of scope.
		// Therefore, pass the test.
	}
}

/**
 * Recreate the conversation.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test11_RecreateConversationForThirdTime() {
	createConversation(getData().getConversationName(), true /*force*/, getData().getTestUser());
}

/**
 * Submit the question 'What drives revenue?'.
 */
@Test
@Dependency({TEST_RECREATE_CONVERSATION_FOR_THIRD_TIME})
public void test12_QuestionWhatDrivesRevenue() {
	submitQuestion(QUESTION_WHAT_DRIVES_REVENUE, SPIRAL /*expectedVisualizationTypeInAnswer*/, false /*isAdditionalInformationAboutVisualizationInAnswer*/, ANSWER_TO_QUESTION_WHAT_DRIVES_REVENUE);
}

/**
 * Delete the conversation.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test13_DeleteConversation() {
	deleteConversation(getData().getConversationName(), getData().getTestUser());
}
}
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
import itest.cloud.ibm.scenario.error.IncorrectAnswerError;
import itest.cloud.ibm.test.scenario.IbmTestScenarioStepRunner;
import itest.cloud.ibm.test.scenario.wxbi.WxbiTestScenarioStep;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * This class defines a set of sanity tests.
 * <p>
 * The following is a list of tests associated with the functionality presented by Kas during his demos:
 * <ul>
 * <li>{@link #test00_CreateConversation()}: Create the conversation.</li>
 * <li>{@link #test01_QuestionWhichRegionHasMostSales()}: Submit the question 'Which region has the most sales?'.</li>
 * <li>{@link #test02_QuestionWhichProductHasBestSalesInThatRegion()}: Submit the question 'Which product has the best sales in that region?'.</li>
 * <li>{@link #test03_QuestionWhatWereTop5SellingProductsInCanadaLastYear()}: Submit the question 'What are the top 5 selling products in Canada last year?'.</li>
 * <li>{@link #test04_QuestionAreThereAnySeasonalTrendsInRevenueBasedOnMonth()}: Submit the question 'Are there any seasonal trends in revenue based on the month?'.</li>
 * <li>{@link #test05_QuestionAreThereAnySeasonalTrendsInRevenueForAllRegionsBasedOnMonth()}: Submit the question 'Are there any seasonal trends in revenue for all regions based on the month?'.</li>
 * <li>{@link #test06_OpenDialVisualizationInRevenueByRegionAndCalendarMonthChart()}: Open the Dial visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.</li>
 * <li>{@link #test07_OpenBoxVisualizationInRevenueByRegionAndCalendarMonthChart()}: Open the Box visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.</li>
 * <li>{@link #test08_OpenPackedBubbleVisualizationInRevenueByRegionAndCalendarMonthChart()}: Open the Packed Bubble visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.</li>
 * <li>{@link #test09_OpenMarimekkoVisualizationInRevenueByRegionAndCalendarMonthChart()}: Open the Marimekko visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.</li>
 * <li>{@link #test10_OpenBarStackedVisualizationInRevenueByRegionAndCalendarMonthChart()}: Open the Bar Stacked visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.</li>
 * <li>{@link #test11_OpenBarVisualizationInRevenueByRegionAndCalendarMonthChart()}: Open the Bar visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.</li>
 * <li>{@link #test12_OpenColumnStackedVisualizationInRevenueByRegionAndCalendarMonthChart()}: Open the Column Stacked visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.</li>
 * <li>{@link #test13_OpenColumnVisualizationInRevenueByRegionAndCalendarMonthChart()}: Open the Column visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.</li>
 * <li>{@link #test14_QuestionWhatImpactsRevenue()}: Submit the question 'What impacts revenue?'.</li>
 * <li>{@link #test15_QuestionWhatKindOfCakeShouldITry()}: Submit the question 'What kind of cake should I try?'.</li>
 * <li>{@link #test16_QuestionWhatWasMyCustomerChurnLastQuarter()}: Submit the question 'What was my customer churn last quarter?'.</li>
 * <li>{@link #test17_DeleteConversation()}: Delete the conversation.</li>
 * </ul>
 * </p>
 */
@RunWith(IbmTestScenarioStepRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StepB06_KasDemoQuestionsTests extends WxbiTestScenarioStep {

	private static final String QUESTION_WHICH_PRODUCT_HAS_BEST_SALES_IN_THAT_REGION = "Which product has the best sales in that region?";
	private static final String[] ANSWER_TO_QUESTION_WHICH_PRODUCT_HAS_BEST_SALES_IN_THAT_REGION = new String[] {"Zone", "Central Europe", "$157,369,344"};

	private static final String QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_BASED_ON_MONTH = "Are there any seasonal trends in revenue based on the month?";

	private static final String QUESTION_WHAT_KIND_OF_CAKE_SHOULD_I_TRY = "What kind of cake should I try?";

	private static final String QUESTION_WHAT_IMPACTS_REVENUE = "What impacts revenue?";
	private static final String QUESTION_WHAT_WERE_TOP_5_SELLING_PRODUCTS_IN_CANADA_LAST_YEAR = "What were the top 5 selling products in Canada last year?";

	private static final String TEST_CREATE_CONVERSATION = CLASS_INDICATOR_OF_DEPENDENCY + ".test00_CreateConversation";
	private static final String TEST_QUESTION_WHICH_REGION_HAS_MOST_SALES = CLASS_INDICATOR_OF_DEPENDENCY + ".test01_QuestionWhichRegionHasMostSales";
	private static final String TEST_QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_BASED_ON_MONTH = CLASS_INDICATOR_OF_DEPENDENCY + ".test05_QuestionAreThereAnySeasonalTrendsInRevenueForAllRegionsBasedOnMonth";

/**
 * Create the conversation.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test00_CreateConversation() {
	createConversation(getData().getConversationName(), true /*force*/, getData().getTestUser());
}

/**
 * Submit the question 'Which region has the most sales?'.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test01_QuestionWhichRegionHasMostSales() {
	submitQuestion(QUESTION_WHICH_REGION_HAS_MOST_SALES, ANSWER_WHICH_REGION_HAS_MOST_SALES);
}

/**
 * Submit the question 'Which product has the best sales in that region?'.
 */
@Test
@Dependency({TEST_QUESTION_WHICH_REGION_HAS_MOST_SALES})
public void test02_QuestionWhichProductHasBestSalesInThatRegion() {
	submitQuestion(QUESTION_WHICH_PRODUCT_HAS_BEST_SALES_IN_THAT_REGION, ANSWER_TO_QUESTION_WHICH_PRODUCT_HAS_BEST_SALES_IN_THAT_REGION);
}

/**
 * Submit the question 'What are the top 5 selling products in Canada last year?'.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test03_QuestionWhatWereTop5SellingProductsInCanadaLastYear() {
	submitQuestion(QUESTION_WHAT_WERE_TOP_5_SELLING_PRODUCTS_IN_CANADA_LAST_YEAR, ANSWER_WHAT_IS_TOP_5_SALES_IN_CANADA_LAST_YEAR);
}

/**
 * Submit the question 'Are there any seasonal trends in revenue based on the month?'.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test04_QuestionAreThereAnySeasonalTrendsInRevenueBasedOnMonth() {
	submitQuestion(QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_BASED_ON_MONTH, COLUMN /*expectedVisualizationTypeInAnswer*/, ANSWER_DIFFICULT_TO_SUMMARIZE_CHART_CREATED);
}

/**
 * Submit the question 'Are there any seasonal trends in revenue for all regions based on the month?'.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test05_QuestionAreThereAnySeasonalTrendsInRevenueForAllRegionsBasedOnMonth() {
	submitQuestion(QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_FOR_ALL_REGIONS_BASED_ON_MONTH, COLUMN /*expectedVisualizationTypeInAnswer*/);
}

/**
 * Open the Dial visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.
 */
@Test
@Dependency({TEST_QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_BASED_ON_MONTH})
public void test06_OpenDialVisualizationInRevenueByRegionAndCalendarMonthChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_FOR_ALL_REGIONS_BASED_ON_MONTH, DIAL, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the Box visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.
 */
@Test
@Dependency({TEST_QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_BASED_ON_MONTH})
public void test07_OpenBoxVisualizationInRevenueByRegionAndCalendarMonthChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_FOR_ALL_REGIONS_BASED_ON_MONTH, BOX, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the Packed Bubble visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.
 */
@Test
@Dependency({TEST_QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_BASED_ON_MONTH})
public void test08_OpenPackedBubbleVisualizationInRevenueByRegionAndCalendarMonthChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_FOR_ALL_REGIONS_BASED_ON_MONTH, PACKED_BUBBLE, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the Marimekko visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.
 */
@Test
@Dependency({TEST_QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_BASED_ON_MONTH})
public void test09_OpenMarimekkoVisualizationInRevenueByRegionAndCalendarMonthChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_FOR_ALL_REGIONS_BASED_ON_MONTH, MARIMEKKO, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the Bar Stacked visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.
 */
@Test
@Dependency({TEST_QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_BASED_ON_MONTH})
public void test10_OpenBarStackedVisualizationInRevenueByRegionAndCalendarMonthChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_FOR_ALL_REGIONS_BASED_ON_MONTH, BAR_STACKED, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the Bar visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.
 */
@Test
@Dependency({TEST_QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_BASED_ON_MONTH})
public void test11_OpenBarVisualizationInRevenueByRegionAndCalendarMonthChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_FOR_ALL_REGIONS_BASED_ON_MONTH, BAR, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the Column Stacked visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.
 */
@Test
@Dependency({TEST_QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_BASED_ON_MONTH})
public void test12_OpenColumnStackedVisualizationInRevenueByRegionAndCalendarMonthChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_FOR_ALL_REGIONS_BASED_ON_MONTH, COLUMN_STACKED, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the Column visualization in the 'Are there any seasonal trends in revenue for all regions based on the month' chart.
 */
@Test
@Dependency({TEST_QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_BASED_ON_MONTH})
public void test13_OpenColumnVisualizationInRevenueByRegionAndCalendarMonthChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_FOR_ALL_REGIONS_BASED_ON_MONTH, COLUMN, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Submit the question 'What impacts revenue?'.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test14_QuestionWhatImpactsRevenue() {
	submitQuestion(QUESTION_WHAT_IMPACTS_REVENUE, SPIRAL /*expectedVisualizationTypeInAnswer*/, false /*isAdditionalInformationAboutVisualizationInAnswer*/, ANSWER_TO_QUESTION_WHAT_DRIVES_REVENUE);
}

/**
 * Submit the question 'What kind of cake should I try?'.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test15_QuestionWhatKindOfCakeShouldITry() {
	try {
		final String question = QUESTION_WHAT_KIND_OF_CAKE_SHOULD_I_TRY;
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
 * Submit the question 'What was my customer churn last quarter?'.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test16_QuestionWhatWasMyCustomerChurnLastQuarter() {
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
 * Delete the conversation.
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test17_DeleteConversation() {
	deleteConversation(getData().getConversationName(), getData().getTestUser());
}
}
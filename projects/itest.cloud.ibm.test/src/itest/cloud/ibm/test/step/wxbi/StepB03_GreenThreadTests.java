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
 * This class defines a set of tests in the green thread.
 * <p>
 * The following is a list of tests associated with this test suite:
 * <ul>
 * <li>{@link #test00_CreateConversation()}: Create the conversation.</li>
 * <li>{@link #test01_QuestionWhichRegionHasMostSales()}: Submit the question 'Which region has the most sales?'.</li>
 * <li>{@link #test02_QuestionWhichProductHasBestSalesInRegionCentralEurope()}: Submit the question 'Which product has the best sales in region Central Europe?'.</li>
 * <li>{@link #test03_QuestionWhatIsTop5SalesInCanadaLastYear()}: Submit the question 'What is the top 5 sales in Canada last year?'.</li>
 * <li>{@link #test05_QuestionWhichRegionHasSalesBelow10000()}: Submit the question 'Which region has sales below $10000?'.</li>
 * <li>{@link #test06_QuestionHowAboutOver100000()}: Submit the question 'How about over $100000?'.</li>
 * <li>{@link #test07_QuestionHowAboutBelow100()}: Submit the question 'How about below $100?'.</li>
 * <li>{@link #test09_QuestionWhichCountryHasHighestRevenueLastYear()}: Submit the question 'Which country has the highest revenue last year?'.</li>
 * <li>{@link #test10_QuestionHowAboutTwoYearsAgo()}: Submit the question 'How about two years ago?'.</li>
 * <li>{@link #test11_DeleteConversation()}: Delete the conversation.</li>
 * </ul>
 * </p>
 */
@RunWith(IbmTestScenarioStepRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StepB03_GreenThreadTests extends WxbiTestScenarioStep {

	private static final String TEST_CREATE_CONVERSATION = CLASS_INDICATOR_OF_DEPENDENCY + ".test00_CreateConversation";
	private static final String TEST_RECREATE_CONVERSATION = CLASS_INDICATOR_OF_DEPENDENCY + ".test04_RecreateConversation";
	private static final String TEST_QUESTION_WHICH_REGION_HAS_SALES_BELOW10000 = CLASS_INDICATOR_OF_DEPENDENCY + ".test05_QuestionWhichRegionHasSalesBelow10000";
	private static final String TEST_RECREATE_CONVERSATION_FOR_SECOND_TIME = CLASS_INDICATOR_OF_DEPENDENCY + ".test08_RecreateConversationForSecondTime";
	private static final String TEST_QUESTION_WHICH_COUNTRY_HAS_HIGHEST_REVENUE_LAST_YEAR = CLASS_INDICATOR_OF_DEPENDENCY + ".test09_QuestionWhichCountryHasHighestRevenueLastYear";

//	private static final String QUESTION_WHAT_IS_AVERAGE_SALES_FOR_PRODUCT_3212762990_IN_CANADA = "What is the average sales for Product 3212762990 in Canada?";
//	private static final String[] ANSWER_WHAT_IS_AVERAGE_SALES_FOR_PRODUCT_3212762990_IN_CANADA = new String[] {"$20,656.46"};

	private static final String QUESTION_WHAT_IS_TOP_5_SALES_IN_CANADA_LAST_YEAR = "What were the top 5 sales in Canada last year?";

	private static final String QUESTION_WHICH_REGION_HAS_SALES_BELOW_10000 = "Which region has sales below $10000?";
	private static final String[] ANSWER_WHICH_REGION_HAS_SALES_BELOW_10000 = new String[] {"Central Europe"};

	private static final String QUESTION_HOW_ABOUT_OVER_100000 = "How about over $100000?";
	private static final String[] ANSWER_HOW_ABOUT_OVER_100000 = new String[] {"Central Europe"};

	private static final String QUESTION_HOW_ABOUT_BELOW_100 = "How about below $100?";
	private static final String[] ANSWER_HOW_ABOUT_BELOW_100 = new String[] {"Americas"};

	private static final String QUESTION_WHICH_COUNTRY_HAS_HIGHEST_REVENUE_LAST_YEAR = "Which country had the highest revenue last year?";
	private static final String[] ANSWER_WHICH_COUNTRY_HAS_HIGHEST_REVENUE_LAST_YEAR = new String[] {"Switzerland", "2023", "$349,478,076"}; // Validated in CA

	private static final String QUESTION_HOW_ABOUT_TWO_YEARS_AGO = "How about two years ago?";
	private static final String[] ANSWER_HOW_ABOUT_TWO_YEARS_AGO = new String[] {"Switzerland", "2022", "$482,114,950"}; // Validated in CA

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
 * <p>
 * This is a simple question with fuzz column mapping.
 * </p>
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test01_QuestionWhichRegionHasMostSales() {
	submitQuestion(QUESTION_WHICH_REGION_HAS_MOST_SALES, ANSWER_WHICH_REGION_HAS_MOST_SALES);
}

/**
 * Submit the question 'Which product has the best sales in region Central Europe?'.
 * <p>
 * This is a question with modifier and categorical filter.
 * </p>
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test02_QuestionWhichProductHasBestSalesInRegionCentralEurope() {
	submitQuestion(QUESTION_WHICH_PRODUCT_HAS_BEST_SALES_IN_REGION_CENTRAL_EUROPE, ANSWER_WHICH_PRODUCT_HAS_BEST_SALES_IN_REGION_CENTRAL_EUROPE);
}

///**
// * Submit the question 'What is the average sales for Product 3212762990 in Canada?'.
// * <p>
// * This is a question with aggregation and filter.
// * </p>
// */
//@Test
//@Ignore
//@Dependency({CREATE_CONVERSATION_TEST})
//public void test03_SubmitQuestionWhatIsAverageSalesForProduct3212762990InCanada() {
//	submitQuestion(QUESTION_WHAT_IS_AVERAGE_SALES_FOR_PRODUCT_3212762990_IN_CANADA, ANSWER_WHAT_IS_AVERAGE_SALES_FOR_PRODUCT_3212762990_IN_CANADA);
//}

/**
 * Submit the question 'What is the top 5 sales in Canada last year?'.
 * <p>
 * This is a question with modifier and time filter.
 * </p>
 */
@Test
@Dependency({TEST_CREATE_CONVERSATION})
public void test03_QuestionWhatIsTop5SalesInCanadaLastYear() {
	submitQuestion(QUESTION_WHAT_IS_TOP_5_SALES_IN_CANADA_LAST_YEAR, ANSWER_WHAT_IS_TOP_5_SALES_IN_CANADA_LAST_YEAR);
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
 * Submit the question 'Which region has sales below $10000?'.
 * <p>
 * This is a question with numerical filters.
 * </p>
 */
@Test
@Dependency({TEST_RECREATE_CONVERSATION})
public void test05_QuestionWhichRegionHasSalesBelow10000() {
	submitQuestion(QUESTION_WHICH_REGION_HAS_SALES_BELOW_10000, ANSWER_WHICH_REGION_HAS_SALES_BELOW_10000);
}

/**
 * Submit the question 'How about over $100000?'.
 * <p>
 * This is a follow-up question.
 * </p>
 */
@Test
@Dependency({TEST_QUESTION_WHICH_REGION_HAS_SALES_BELOW10000})
public void test06_QuestionHowAboutOver100000() {
	submitQuestion(QUESTION_HOW_ABOUT_OVER_100000, ANSWER_HOW_ABOUT_OVER_100000);
}

/**
 * Submit the question 'How about below $100?'.
 * <p>
 * This is a follow-up question.
 * </p>
 */
@Test
@Dependency({TEST_QUESTION_WHICH_REGION_HAS_SALES_BELOW10000})
public void test07_QuestionHowAboutBelow100() {
	submitQuestion(QUESTION_HOW_ABOUT_BELOW_100, ANSWER_HOW_ABOUT_BELOW_100);
}

/**
 * Recreate the conversation for the second time.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test08_RecreateConversationForSecondTime() {
	createConversation(getData().getConversationName(), true /*force*/, getData().getTestUser());
}

/**
 * Submit the question 'Which country has the highest revenue last year?'.
 */
@Test
@Dependency({TEST_RECREATE_CONVERSATION_FOR_SECOND_TIME})
public void test09_QuestionWhichCountryHasHighestRevenueLastYear() {
	submitQuestion(QUESTION_WHICH_COUNTRY_HAS_HIGHEST_REVENUE_LAST_YEAR, ANSWER_WHICH_COUNTRY_HAS_HIGHEST_REVENUE_LAST_YEAR);
}

/**
 * Submit the question 'How about two years ago?'.
 * <p>
 * This is a follow-up question.
 * </p>
 */
@Test
@Dependency({TEST_QUESTION_WHICH_COUNTRY_HAS_HIGHEST_REVENUE_LAST_YEAR})
public void test10_QuestionHowAboutTwoYearsAgo() {
	submitQuestion(QUESTION_HOW_ABOUT_TWO_YEARS_AGO, ANSWER_HOW_ABOUT_TWO_YEARS_AGO);
}

/**
 * Delete the conversation.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test11_DeleteConversation() {
	deleteConversation(getData().getConversationName(), getData().getTestUser());
}
}
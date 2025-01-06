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

import itest.cloud.ibm.test.scenario.IbmTestScenarioConstants;

/**
 * Common constants for the WXBI scenario.
 * <p>
 * This class holds constants specific to the scenario.
 * </p>
 */
public interface WxbiTestScenarioConstants extends IbmTestScenarioConstants {

	String PREREQUISITE_TESTS = PACKAGE_INDICATOR_OF_DEPENDENCY + ".StepA01_PrerequisiteTests";
	String TEST_SET_ACCOUNT = PREREQUISITE_TESTS + ".test01_SetAccount";

//	String PREREQUISITES_TESTS_STEP = PACKAGE_INDICATOR_OF_DEPENDENCY + ".Step02_PrerequisitesTests";
//	String CREATE_CONVERSATION_TEST = PREREQUISITES_TESTS_STEP + ".test01_CreateConversation";

	String REVENUE_BY_PRODUCT_LINE_METRIC = "Revenue by Product Line";
	String REVENUE_FOR_LAST_2_YEARS_METRIC = "Revenue For Last 2 years";
	String TRAKKER_REVENUE_FOR_LAST_YEAR_METRIC = "Trakker Revenue for Last Year";
	String REVENUE_COMPARED_TO_PLANNED_REVENUE_METRIC = "Revenue compared to Planned Revenue";

	String[] DATA_SOURCES = new String[] {"GOSALES_AND_FORECAST"};
	String[] ERRONEOUS_KEYWORDS_IN_CONTEXT = new String[] {"[object Object]"};

	String QUESTION_WHICH_REGION_HAS_MOST_SALES = "Which region has the most sales?";
	String[] ANSWER_WHICH_REGION_HAS_MOST_SALES = new String[] {"Central Europe", "$2,143,494,784"};

	String QUESTION_WHICH_PRODUCT_HAS_BEST_SALES_IN_REGION_CENTRAL_EUROPE = "Which product has the best sales in region Central Europe?";
	String[] ANSWER_WHICH_PRODUCT_HAS_BEST_SALES_IN_REGION_CENTRAL_EUROPE = new String[] {"Zone", "$157,369,344.95"};

	String QUESTION_WHAT_WAS_MY_CUSTOMER_CHURN_LAST_QUARTER = "What was my customer churn last quarter?";
	String[] ANSWER_TO_QUESTION_OUT_OF_SCOPEN = new String[] {"different question"};

	String QUESTION_ARE_THERE_ANY_SEASONAL_TRENDS_IN_REVENUE_FOR_ALL_REGIONS_BASED_ON_MONTH = "Are there any seasonal trends in revenue for all regions based on the month with the title \"Seasonal Trends in Revenue for All Regions\"?";
	String QUESTION_DRAW_REVENUE_BY_PRODUCT_LINE = "Draw revenue by product line with the title \"Revenue by Product Line\"";

	String[] ANSWER_WHAT_IS_TOP_5_SALES_IN_CANADA_LAST_YEAR = new String[] {"Star Lite", "Star Gazer 2", "Hailstorm Titanium Woods Set", "Canyon Mule Journey Backpack", "Star Dome"};
	String[] ANSWER_DIFFICULT_TO_SUMMARIZE_CHART_CREATED = new String[] {"difficult to summarize", "chart was created"};

	String[] ANSWER_TO_QUESTION_WHAT_DRIVES_REVENUE = new String[] {/*"ProductBrand", "PlannedRevenue"*/};
}
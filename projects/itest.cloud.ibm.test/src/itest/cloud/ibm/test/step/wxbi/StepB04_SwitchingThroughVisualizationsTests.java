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
import itest.cloud.ibm.test.scenario.IbmTestScenarioStepRunner;
import itest.cloud.ibm.test.scenario.wxbi.WxbiTestScenarioStep;

/**
 * This class defines a set of tests on switching through various visualizations of a chart.
 * <p>
 * The following is a list of tests associated with this test suite:
 * <ul>
 * <li>{@link #test00_CreateConversation()}: Create the conversation.</li>
 * <li>{@link #test01_GenerateRevenueByProductLineChart()}: Generate the 'Revenue by Product Line' chart.</li>
 * <li>{@link #test02_OpenListVisualizationInRevenueByProductLineChart()}: Open the List visualization in the 'Revenue by Product Line' chart.</li>
 * <li>{@link #test03_OpenCrossTabVisualizationInRevenueByProductLineChart()}: Open the CrossTab visualization in the 'Revenue by Product Line' chart.</li>
 * <li>{@link #test04_OpenRadarVisualizationInRevenueByProductLineChart()}: Open the Radar visualization in the 'Revenue by Product Line' chart.</li>
 * <li>{@link #test05_OpenWordCloudAnalysisVisualizationInRevenueByProductLineChart()}: Open the WordCloud visualization in the 'Revenue by Product Line' chart.</li>
 * <li>{@link #test07_OpenBoxVisualizationInRevenueByProductLineChart()}: Open the Box visualization in the 'Revenue by Product Line' chart.</li>
 * <li>{@link #test08_OpenPackedBubbleVisualizationInRevenueByProductLineChart()}: Open the Packed Bubble visualization in the 'Revenue by Product Line' chart.</li>
 * <li>{@link #test09_OpenPointVisualizationInRevenueByProductLineChart()}: Open the Point visualization in the 'Revenue by Product Line' chart.</li>
 * <li>{@link #test12_DeleteConversation()}: Delete the conversation.</li>
 * </ul>
 * </p>
 */
@RunWith(IbmTestScenarioStepRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StepB04_SwitchingThroughVisualizationsTests extends WxbiTestScenarioStep {

	private static final String CREATE_CONVERSATION_TEST = CLASS_INDICATOR_OF_DEPENDENCY + ".test00_CreateConversation";
	private static final String GENERATE_REVENUE_BY_PRODUCT_LINE_CHART_TEST = CLASS_INDICATOR_OF_DEPENDENCY + ".test01_GenerateRevenueByProductLineChart";

/**
 * Create the conversation.
 */
@Test
@Dependency({TEST_SET_ACCOUNT})
public void test00_CreateConversation() {
	createConversation(getData().getConversationName(), true /*force*/, getData().getTestUser());
}

/**
 * Generate the 'Revenue by Product Line' chart.
 */
@Test
@Dependency({CREATE_CONVERSATION_TEST})
public void test01_GenerateRevenueByProductLineChart() {
	submitQuestion(getData().getConversationName(), QUESTION_DRAW_REVENUE_BY_PRODUCT_LINE, COLUMN, getData().getTestUser());
}

/**
 * Open the List visualization in the 'Revenue by Product Line' chart.
 */
@Test
@Dependency({GENERATE_REVENUE_BY_PRODUCT_LINE_CHART_TEST})
public void test02_OpenListVisualizationInRevenueByProductLineChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_DRAW_REVENUE_BY_PRODUCT_LINE, LIST, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the CrossTab visualization in the 'Revenue by Product Line' chart.
 */
@Test
@Dependency({GENERATE_REVENUE_BY_PRODUCT_LINE_CHART_TEST})
public void test03_OpenCrossTabVisualizationInRevenueByProductLineChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_DRAW_REVENUE_BY_PRODUCT_LINE, CROSS_TAB, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the Radar visualization in the 'Revenue by Product Line' chart.
 */
@Test
@Dependency({GENERATE_REVENUE_BY_PRODUCT_LINE_CHART_TEST})
public void test04_OpenRadarVisualizationInRevenueByProductLineChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_DRAW_REVENUE_BY_PRODUCT_LINE, RADAR, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the WordCloud visualization in the 'Revenue by Product Line' chart.
 */
@Test
@Dependency({GENERATE_REVENUE_BY_PRODUCT_LINE_CHART_TEST})
public void test05_OpenWordCloudAnalysisVisualizationInRevenueByProductLineChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_DRAW_REVENUE_BY_PRODUCT_LINE, WORD_CLOUD, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the Dial visualization in the 'Revenue by Product Line' chart.
 */
@Test
@Dependency({GENERATE_REVENUE_BY_PRODUCT_LINE_CHART_TEST})
public void test06_OpenDialAnalysisVisualizationInRevenueByProductLineChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_DRAW_REVENUE_BY_PRODUCT_LINE, DIAL, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the Box visualization in the 'Revenue by Product Line' chart.
 */
@Test
@Dependency({GENERATE_REVENUE_BY_PRODUCT_LINE_CHART_TEST})
public void test07_OpenBoxVisualizationInRevenueByProductLineChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_DRAW_REVENUE_BY_PRODUCT_LINE, BOX, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the Packed Bubble visualization in the 'Revenue by Product Line' chart.
 */
@Test
@Dependency({GENERATE_REVENUE_BY_PRODUCT_LINE_CHART_TEST})
public void test08_OpenPackedBubbleVisualizationInRevenueByProductLineChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_DRAW_REVENUE_BY_PRODUCT_LINE, PACKED_BUBBLE, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the Point visualization in the 'Revenue by Product Line' chart.
 */
@Test
@Dependency({GENERATE_REVENUE_BY_PRODUCT_LINE_CHART_TEST})
public void test09_OpenPointVisualizationInRevenueByProductLineChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_DRAW_REVENUE_BY_PRODUCT_LINE, POINT, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the Bar visualization in the 'Revenue by Product Line' chart.
 */
@Test
@Dependency({GENERATE_REVENUE_BY_PRODUCT_LINE_CHART_TEST})
public void test10_OpenBarVisualizationInRevenueByProductLineChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_DRAW_REVENUE_BY_PRODUCT_LINE, BAR, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Open the Column visualization in the 'Revenue by Product Line' chart.
 */
@Test
@Dependency({GENERATE_REVENUE_BY_PRODUCT_LINE_CHART_TEST})
public void test11_OpenColumnVisualizationInRevenueByProductLineChart() {
	openVisualizationInAnswer(getData().getConversationName(), QUESTION_DRAW_REVENUE_BY_PRODUCT_LINE, COLUMN, false /*isAdditionalInformationAboutVisualizationInAnswer*/, getData().getTestUser());
}

/**
 * Delete the conversation.
 */
@Test
@Dependency({CREATE_CONVERSATION_TEST})
public void test12_DeleteConversation() {
	deleteConversation(getData().getConversationName(), getData().getTestUser());
}
}
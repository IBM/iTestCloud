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
package itest.cloud.ibm.test.step.ca;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import itest.cloud.ibm.page.ca.CaContentPage;
import itest.cloud.ibm.page.element.ca.CaContentElement;
import itest.cloud.ibm.page.element.ca.CaContentTabElement;
import itest.cloud.ibm.test.scenario.IbmTestScenarioStepRunner;
import itest.cloud.ibm.test.scenario.ca.CaTestScenarioStep;

/**
 * This class defines a set of tests to validate the prerequisites and their associated functionality.
 * <p>
 * The following is a list of tests associated with this test suite:
 * <ul>
 * <li>{@link #test01_OpenHomePage()}: Set the cloud account associated with the default user.</li>
 * </ul>
 * </p>
 */
@RunWith(IbmTestScenarioStepRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Step01_PrerequisitesTests extends CaTestScenarioStep {

/**
 * Open the home page.
 */
@Test
public void test01_OpenHomePage() {
	openHomePage(getTestUser());
}

/**
 * Log out.
 */
@Test
public void test02_AdhocTest() {
	CaContentPage contentPage = openContentPage(getTestUser());
	CaContentTabElement teamContentTab = contentPage.openTeamContentTab();
	CaContentElement contentElement = teamContentTab.getContentElement("Jasna/Tornado", true /*fail*/);
	contentElement.open();
}

/**
 * Log out.
 */
@Test
public void test99_Logout() {
	getCurrentPage().logout();
}
}
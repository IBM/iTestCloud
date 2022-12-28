/*********************************************************************
 * Copyright (c) 2017, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.acme.steps;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import com.ibm.itest.cloud.acme.scenario.AcmeSanityScenarioStep;
import com.ibm.itest.cloud.acme.scenario.AcmeSanityScenarioStepRunner;

/**
 * This class defines a sample sanity test suite.
 * <p>
 * The following is a list of tests associated with this test suite:
 * <ul>
 * <li>{@link #test01_OpenHomePage()}: Opens the home page.</li>
 * </ul>
 * </p>
 */
@RunWith(AcmeSanityScenarioStepRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class Step01_SanityTests extends AcmeSanityScenarioStep {

/**
 * Opens the home page.
 */
@Test
public void test01_OpenHomePage() {
	openHomePage(getData().getTestUser());
}
}
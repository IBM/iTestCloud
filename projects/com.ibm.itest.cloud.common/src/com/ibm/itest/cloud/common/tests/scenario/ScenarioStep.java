/*********************************************************************
 * Copyright (c) 2012, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.common.tests.scenario;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.*;

import org.junit.*;
import org.junit.runners.Suite.SuiteClasses;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import com.ibm.itest.cloud.common.tests.config.Config;
import com.ibm.itest.cloud.common.tests.topology.Topology;
import com.ibm.itest.cloud.common.tests.web.WebPage;
import com.ibm.itest.cloud.common.tests.web.browsers.WebBrowser;

/**
 * Manage a list of tests to execute in a scenario step.
 * <p>
 * Scenario may have several steps which are defined using a specific {@link ScenarioRunner}
 * and a list of classes as argument of {@link SuiteClasses} annotation.
 * </p><p>
 * The step provides easy access to scenario configuration and data through its
 * {@link ScenarioExecution} stored instance.
 * </p><p>
 * This step is connected to a web page. The page might be stored by the step
 * when loaded. If so, it's automatically stored to the {@link ScenarioExecution}
 * at the end of the test execution to allow next test or step to have the last
 * page used by previous step in hand when starting.
 * </p><p>
 * The step also stores all workaround used during the tests and can provide
 * information about them.
 * </p>
 * Design: To be finalized
 */
public class ScenarioStep {

	class ScenarioStepRule implements org.junit.rules.MethodRule {

		final class ScenarioStepRuleStatement extends Statement {
			private final Statement statement;
			private final FrameworkMethod method;
			private final Object target;

			ScenarioStepRuleStatement(final Statement statement, final FrameworkMethod method, final Object target) {
				this.statement = statement;
				this.method = method;
				this.target = target;
			}

			@Override
			public void evaluate() throws Throwable {
				ScenarioStep.this.scenarioExecution.runTest(this.statement, this.method, this.target, IS_NEW_STEP);
			}

			public void setExecution(final ScenarioExecution execution) {
				ScenarioStep.this.scenarioExecution = execution;
			}
		}

		@Override
		public Statement apply(final Statement statement, final FrameworkMethod method, final Object target) {
			return new ScenarioStepRuleStatement(statement, method, target);
		}
	}

	// Step info
	protected static boolean IS_NEW_STEP = true;

	// Execution
	protected ScenarioExecution scenarioExecution;


@Rule
public ScenarioStepRule stepRule = new ScenarioStepRule();

@BeforeClass
public static void setUpStep() {
	IS_NEW_STEP = true;
}

/**
 * @see Config#getBrowser()
 */
protected WebBrowser getBrowser() {
	return getConfig().getBrowser();
}

/**
 * @see ScenarioExecution#getConfig()
 */
protected Config getConfig() {
	return this.scenarioExecution.config;
}

/**
 * @see ScenarioExecution#getData()
 */
protected ScenarioData getData() {
	return this.scenarioExecution.data;
}

/**
 * The current test page or the page stored by the {@link ScenarioExecution}
 * if none was already stored.
 *
 * @return The page as a subclass of {@link WebPage}. May be <code>null</code>
 * if no page was stored neither in current test nor in previous one.
 * TODO Use {@link #getCurrentPage()} instead.
 */
protected WebPage getPage() {
//	if (this.page == null) {
//		this.page = getScenarioExecution().getPage();
//	}
//	return this.page;
	println("INFO: ScenarioStep.getPage() method is still used although getCurrentPage() should be used instead.");
	printStackTrace(1);
	return getCurrentPage();
}

/**
 * The current test page or the page stored by the {@link ScenarioExecution}
 * if none was already stored.
 *
 * @return The page as a subclass of {@link WebPage}. May be <code>null</code>
 * if no page was stored neither in current test nor in previous one.
 */
protected WebPage getCurrentPage() {
//	return getBrowser().getCurrentPage();
	return WebPage.getCurrentPage();
}

/**
 * Return the scenario execution.
 *
 * @return The scenario execution as a {@link ScenarioExecution}.
 */
protected ScenarioExecution getScenarioExecution() {
	return this.scenarioExecution;
}

/**
 * @see ScenarioExecution#getTopology()
 */
protected Topology getTopology() {
	return getConfig().getTopology();
}

///**
// * Setup executed at the beginning of each test step.
// * <p>
// * So far, it only displays the step title when it's the first test and the test
// * title in the console.
// * </p>
// */
//@Before
//public void setUpTest() {
//
//	// Print step title
//	if (IS_FIRST_TEST) {
//		printStepStart(getScenarioExecution().stepName);
//	}
//
//	// Test case starting point
//	println("	- "+TIME_FORMAT.format(new Date(System.currentTimeMillis()))+": start test case '"+getScenarioExecution().testName+"'...");
//}

/**
 * Sleep for a given number of seconds if the server is considered slow.
 * <p>
 * Nothing happens if the server is not considered slow.
 * </p>
 * @param time Number of seconds the execution will sleep, if the server
 * is considered slow.
 */
public void sleepIfSlowServer(final int time) {
	if (this.scenarioExecution.isSlowServer()) {
		if (DEBUG) println("		+ Slow server: sleeping for '" + time + "' seconds.");
		sleep(time);
	}
}

/**
 * Tear down executed at the end of each test step.
 * <p>
 * So far, it turn off the first step flag and stores the current page to the
 * scenario execution to pass it to next test.
 * </p>
 */
@After
public void tearDownTest() throws Exception {
	IS_NEW_STEP = false;
}
}

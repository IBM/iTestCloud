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
package itest.cloud.scenario;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.*;

import itest.cloud.utils.FailureBlocker;
import itest.cloud.utils.FailureRelaxer;

/**
 * Manage scenario step JUnit run.
 * <p>
 * This specific JUnit 4 runner allow to identify critical tests which will imply
 * a scenario execution stop if a failure would occur in those tests.
 * </p>
 */
public abstract class ScenarioStepRunner extends BlockJUnit4ClassRunner {

	protected ScenarioExecution scenarioExecution;
	private List<FrameworkMethod> criticalTests;
	private boolean shouldEndExecution = false;
	private int testCounter = 0;

public ScenarioStepRunner(final Class< ? > klass) throws InitializationError {
	super(klass);

	// Extract critical tests
	this.criticalTests = extractCriticalTests();
}

private List<FrameworkMethod> extractCriticalTests() {
	// Check whether the step is mandatory
	TestClass klass = getTestClass();
	boolean mandatoryClass = false;
	for (Annotation annotation: klass.getAnnotations()) {
		if (annotation.annotationType().equals(FailureBlocker.class)) {
			mandatoryClass = true;
			break;
		}
	}

	// Check whether there are any mandatory tests
	List<FrameworkMethod> tests = klass.getAnnotatedMethods(Test.class);
	List<FrameworkMethod> mandatoryTests;
	if (mandatoryClass) {
		mandatoryTests = new ArrayList<FrameworkMethod>(tests);
		mandatoryTests.removeAll(klass.getAnnotatedMethods(FailureRelaxer.class));
	} else {
		mandatoryTests = new ArrayList<FrameworkMethod>();
		for (FrameworkMethod mandatoryTest: klass.getAnnotatedMethods(FailureBlocker.class)) {
			if (tests.contains(mandatoryTest)) {
				mandatoryTests.add(mandatoryTest);
			}
		}
	}
	return mandatoryTests;
}

/**
 * @return the scenarioExecution
 */
public ScenarioExecution getScenarioExecution() {
	if (this.scenarioExecution == null) {
		this.shouldEndExecution = true;
		startExecution();
	}
	return this.scenarioExecution;
}

/**
 * {@inheritDoc}
 * <p>
 * Store the scenario execution object inside the statement given to the test.
 * </p>
 */
@Override
protected Statement methodBlock(final FrameworkMethod method) {
	ScenarioStep.ScenarioStepRule.ScenarioStepRuleStatement stepRuleStatement = (ScenarioStep.ScenarioStepRule.ScenarioStepRuleStatement) super.methodBlock(method);
	stepRuleStatement.setExecution(this.scenarioExecution);
	return stepRuleStatement;
}

/**
 * {@inheritDoc}
 * <p>
 * Skip the child run if it should stop.
 * </p>
 */
@Override
protected void runChild(final FrameworkMethod method, final RunNotifier notifier) {
	if (!getScenarioExecution().shouldStop()) {
		super.runChild(method, notifier);
	}
	if (++this.testCounter == testCount() && this.shouldEndExecution) {
		this.scenarioExecution.finish();
	}
}

/**
 * Store the scenario execution.
 *
 * @param execution the scenarioExecution to set
 */
public void setScenarioExecution(final ScenarioExecution execution) {
	this.scenarioExecution = execution;

	// Add the current step mandatory tests to the scenario execution
	this.scenarioExecution.addMandatoryTests(this.criticalTests);
}

/**
 * Start the scenario execution.
 * <p>
 * Subclasses needs to override this action in order to initialize their specific
 * configuration and data.
 * </p>
 */
abstract protected void startExecution();

}

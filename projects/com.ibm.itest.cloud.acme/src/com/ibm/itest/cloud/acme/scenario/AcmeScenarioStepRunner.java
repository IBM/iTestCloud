/*********************************************************************
 * Copyright (c) 2018, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.acme.scenario;

import java.util.Comparator;

import org.junit.runner.Description;
import org.junit.runner.manipulation.Sorter;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import com.ibm.itest.cloud.common.tests.scenario.ScenarioStepRunner;

/**
 * Manage a scenario step JUnit run.
 * <p>
 * This class overrides {@link #getChildren()} to sort test by their name.
 * </p>
 */
public class AcmeScenarioStepRunner extends ScenarioStepRunner {

	/**
	 * The step tests sorter class.
	 * <p>
	 * Default is to sort tests using alphabetical ascending order.
	 * </p>
	 */
	class ClmScenarioStepTestsSorter extends Sorter {
		public ClmScenarioStepTestsSorter() {
			super(new Comparator<Description>() {
				@Override
				public int compare(final Description arg0, final Description arg1) {
					return arg0.getMethodName().compareTo(arg1.getMethodName());
				}
			});
		}
	}

	/**
	 * The test comparator.
	 * <p>
	 * Strangely, having set the runner sorter seems not to be enough with JUnit 4.8.1. We need this additional comparator to sort the children list (see {@link #getChildren()}).
	 * </p>
	 */
	Comparator<FrameworkMethod> testsComparator;

	/**
	 * The test sorter used by JUnit parent classes (see {@link ParentRunner}).
	 */
	protected ClmScenarioStepTestsSorter testsSorter;

public AcmeScenarioStepRunner(final Class< ? > klass) throws InitializationError {
	super(klass);
	sort(initTestsSorter());
}

/**
 * Return the test methods comparator.
 *
 * @return the test methods comparator as a {@link Comparator} of {@link FrameworkMethod}.
 */
protected Comparator<FrameworkMethod> getTestsComparator() {
	if (this.testsComparator == null) {
		initTestsComparator();
	}
	return this.testsComparator;
}

/**
 * Initialize the test methods comparator.
 * <p>
 * Might be overridden by subclass, but in that case, the test methods comparator should match the comparator of the tests sorter (see {@link #initTestsSorter()}).
 * </p>
 *
 * @return The initialized test methods comparator as a {@link Comparator} of {@link FrameworkMethod}.
 */
protected Comparator<FrameworkMethod> initTestsComparator() {
	return this.testsComparator = new Comparator<FrameworkMethod>() {
		@Override
		public int compare(final FrameworkMethod m1, final FrameworkMethod m2) {
			return m1.getName().compareTo(m2.getName());
		}
	};
}

/**
 * Initialize the tests sorter.
 * <p>
 * Might be overridden by subclass, but in that case, the comparator of the tests sorter should match the test methods comparator (see {@link #initTestsComparator()}).
 * </p>
 *
 * @return The initialized tests sorter as a {@link Sorter}.
 */
protected Sorter initTestsSorter() {
	return this.testsSorter = new ClmScenarioStepTestsSorter();
}

@Override
protected void startExecution() {
	this.scenarioExecution = new AcmeScenarioExecution();
}

}

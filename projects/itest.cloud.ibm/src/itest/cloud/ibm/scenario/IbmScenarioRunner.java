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
package itest.cloud.ibm.scenario;

import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

import itest.cloud.scenario.ScenarioRunner;

/**
 * Manage the scenario JUnit run.
 */
abstract public class IbmScenarioRunner extends ScenarioRunner implements IbmScenarioConstants {

public IbmScenarioRunner(final Class< ? > klass, final RunnerBuilder builder) throws InitializationError {
	super(klass, builder);
}

/**
 * Return the scenario data.
 *
 * @return The data as a {@link IbmScenarioData}.
 */
protected IbmScenarioData getScenarioData(){
	return getScenarioExecution().getData();
}

@Override
public IbmScenarioExecution getScenarioExecution() {
	return (IbmScenarioExecution) super.getScenarioExecution();
}

@Override
protected void initAnnotationFilters() {
	super.initAnnotationFilters();

//	final WxbiApplication wxbiApplication = getScenarioExecution().getTopology().getWxbiApplication();
//	addAnnotationFilter(new AnnotationFilter<CloudTest>(wxbiApplication.isCloudApplication(), PARAMETER_APPLICATIONS, CloudTest.class));
}
}
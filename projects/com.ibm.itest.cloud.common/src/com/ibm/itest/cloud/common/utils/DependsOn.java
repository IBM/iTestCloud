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
package com.ibm.itest.cloud.common.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to identify step and/or test which depends on other step or test.
 * <p>
 * The first implementation for this annotation does not support argument. Which
 * means the main target of this annotation is to tell the scenario that such
 * annotated step or test cannot be run as a first step or test.
 * </p><p>
 * Later, we can improve this annotation to accept classe and method arguments
 * in order to check that the steps and tests that the annotated test depends on
 * are in the steps and tests list prior the test addition to the JUnit test suite.
 * </p>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface DependsOn {

}

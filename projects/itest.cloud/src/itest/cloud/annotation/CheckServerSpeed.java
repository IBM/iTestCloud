/*********************************************************************
 * Copyright (c) 2015, 2022 IBM Corporation and others.
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
package itest.cloud.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation to identify whether a test will also check whether or not the server
 * is considered slow; argument indicates the max time (in seconds) that this
 * specific test should take.  A test that takes longer indicates a slower server;
 * a test that takes less time indicates the server is behaving normally.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckServerSpeed {
	// Max time for some specific test, in seconds.
	int value();
}

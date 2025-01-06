/*********************************************************************
 * Copyright (c) 2018, 2024 IBM Corporation and others.
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
package itest.cloud.ibm.config;

import static itest.cloud.scenario.ScenarioUtil.getParameterValue;

import itest.cloud.config.User;

/**
 * This class defines and manages a user.
 */
public class IbmUser extends User implements IbmUserConstants {

	private final String namespace;

public IbmUser(final String prefix) {
	super(prefix, /*encrypted*/ true);
	this.namespace = (prefix != null) ? getParameterValue(prefix + NAMESPACE, DEFAULT_NAMESPACE) : null;
}

/**
 * Return the namespace of the user.
 *
 * @return The namespace as {@link String}
 */
public String getNamespace() {
	return this.namespace;
}
}
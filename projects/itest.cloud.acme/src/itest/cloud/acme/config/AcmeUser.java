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
package itest.cloud.acme.config;

import static itest.cloud.scenario.ScenarioUtils.getParameterBooleanValue;

import itest.cloud.config.User;

/**
 * This class defines and manages a user.
 */
public class AcmeUser extends User {

	private final boolean federated;

public AcmeUser(final String prefix) {
	super(prefix, /*encrypted:*/true);
	this.federated = getParameterBooleanValue(prefix + "AccountFederated");
}

public AcmeUser(final String userId, final String userName, final String pwd, final String mail, final boolean federated) {
	super(userId, userName, pwd, mail);
	this.federated = federated;
}

/**
 * Specifies whether the user account is associated with an IBM w3ID (federated).
 *
 * @return <code>true</code> if the user account is associated with an IBM w3ID or <code>false</code> otherwise.
 */
public boolean isFederated() {
	return this.federated;
}
}
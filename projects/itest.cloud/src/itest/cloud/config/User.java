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
package itest.cloud.config;

import static itest.cloud.scenario.ScenarioUtils.*;
import static itest.cloud.utils.EncryptionUtils.decrypt;

import org.apache.commons.codec.EncoderException;

import itest.cloud.scenario.errors.ScenarioFailedError;

/**
 * User connected to an application while going to a web page.
 * <p>
 * User is defined by an ID which is a unique string. It has a name, a password and
 * an email address.
 * </p>
 */
public class User implements UserConstants, IUser {

	public static void main(final String[] args) throws EncoderException {
		if((args == null) || (args.length == 0)) {
			System.err.println("A password was not provided to encode");
		}
		else {
			System.out.println("Encoded password: " + decrypt(args[0]));
		}
	}
	// User info
	String id;
	String name;

	String password;
	String email;

	// Encryption
	private boolean encrypted = false;

protected User(final String prefix) {
	this(prefix, null);
}

protected User(final String prefix, final boolean encrypted) {
	this(prefix);
	this.encrypted = encrypted;
}

protected User(final String prefix, final String user) {
	String defaultDomain = getParameterValue(USER_DEFAULT_EMAIL_DOMAIN_PROPERTY, USER_DEFAULT_EMAIL_DOMAIN_VALUE);
	if (prefix == null) {
		this.name = user;
		this.id = user;
		this.password = user;
		this.email = user+defaultDomain;
	} else {
		this.name = getParameterValue(prefix+USERNAME_ID, user);
		this.id = getParameterValue(prefix+USERID_ID, this.name);
		this.password = getPasswordValue(prefix+PASSWORD_ID);
		if (this.password == null) {
			this.password = this.id;
		}
		this.email = getParameterValue(prefix+EMAIL_ID, this.id+getParameterValue(MAIL_DOMAIN_ID, defaultDomain));
	}

	// Check that we got at least an ID, a name and a password
	if (this.name == null || this.id == null || this.password == null) {
		StringBuilder messageBuilder = new StringBuilder("Missing user information:");
		String separator = EMPTY_STRING;
		if (this.id == null) {
			messageBuilder.append(" ID");
			separator = ", ";
		}
		if (this.name == null) {
			messageBuilder.append(separator).append(" name");
			separator = ", ";
		}
		if (this.password == null) {
			messageBuilder.append(separator).append(" password");
			separator = ", ";
		}
		messageBuilder.append(" cannot be null.");
		throw new ScenarioFailedError(messageBuilder.toString());
	}

	// Warn if email has not been defined
	if (this.email == null) {
		println("Warning: no email has been defined for user "+this.id);
	}
}

protected User(final String userId, final String userName, final String pwd, final String mail) {
	this.id = userId;
	this.name = userName;
	this.password = pwd;
	this.email = mail;
}

@Override
public boolean equals(final Object obj) {
	if (obj instanceof User) {
	    return ((User)obj).id.equals(this.id);
	}
	return false;
}

/**
 * Return the user email address.
 *
 * @return The email address as a {@link String}
 */
@Override
final public String getEmail() {
	return this.email;
}

/**
 * Return the first name of the user.
 *
 * @return The first name of the user.
 */
public final String getFirstName(){
	return getName().split(SPACE_STRING)[0];
}

/**
 * Return the user ID.
 *
 * @return The ID as a {@link String}
 */
@Override
final public String getId() {
	return this.id;
}

/**
 * Return the first name of the user.
 *
 * @return The first name of the user.
 */
final public String getLastName() {
	String[] nameArray = getName().split(SPACE_STRING);
	return nameArray[nameArray.length - 1];
}

/**
 * Return the user name.
 *
 * @return The name as a {@link String}
 */
@Override
final public String getName() {
	return this.name;
}

/**
 * Return the user password.
 *
 * @return The password as a {@link String}
 */
@Override
final public String getPassword() {
	if (this.encrypted) {
		return decrypt(this.password);
	}
	return this.password;
}

@Override
public int hashCode() {
    return this.id.hashCode();
}

/**
 * Specify whether a last name is available for the user.
 *
 * @return <code>true</code> if a last name is available for the user or <code>false</code> otherwise.
 */
final public boolean isLastNameAvailable(){
	return getName().contains(SPACE_STRING);
}

@Override
public boolean matches(final IUser user) {
    return this.id.equals(((User)user).id);
}

@Override
public String toString() {
	return "User id=" + this.id + ", name=" + this.name + ", passwd=" + this.password.charAt(0) + "*******" + (this.email == null ? "" : ", mail=" + this.email);
}
}
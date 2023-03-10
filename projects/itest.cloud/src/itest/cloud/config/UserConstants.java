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

public interface UserConstants {
	String USERID_ID = "UserID";
	String USERNAME_ID = "Username";
	String PASSWORD_ID = "Password";
	String EMAIL_ID = "Email";
	String MAIL_DOMAIN_ID = "mailDomain";

	// Default domain
	String USER_DEFAULT_EMAIL_DOMAIN_PROPERTY = "user.default.email.domain";
	String USER_DEFAULT_EMAIL_DOMAIN_VALUE = "@ibm.com";
}

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
package itest.cloud.topology;

import static itest.cloud.scenario.ScenarioUtil.*;

import java.net.*;
import java.util.StringTokenizer;

import itest.cloud.config.User;
import itest.cloud.scenario.error.ScenarioFailedError;

/**
 * Abstract class for a topology application.
 * <p>
 * An application is identified by its {@link #location} which is assumed to be
 * the prefix for any web page URL of this application.
 * </p><p>
 * It's also assumed that this location is the concatenation of two strings:
 * <ol>
 * <li>the server address: expected format is <code>https:<i>Server_DNS_Name</i>:<i>port_value</i></code>
 * (e.g. <code>https://jbslnxvh02.ottawa.ibm.com:9443</code>)</li>
 * <li>the context root: usually a simple name (e.g. <code>jts</code>)</li>
 * </ol>
 * </p><p>
 * A user might be stored in the application let the topology know who is
 * connected to this application.
 * </p><p>
 * An application is responsible to provide web pages address to client.
 * </p><p>
 * Following functionalities are also defined by this page:
 * <ul>
 * <li>{@link #getPageUrl(String)}: Return the page URL from
 * the given link.</li>
 * <li>{@link #getTitle()}: Return the application title.</li>
 * </ul>
 * </p>
 */
abstract public class Application {

	// The license used by the application
	private static final String[] NO_LICENSE = new String[0];
	// The url prefix for any web pages of this application
	private URL appliUrl;
	String location;
	String server;

	String contextRoot;

	// The user connected to the application
	User user;
	protected String license;
	private String name;

protected Application(final String url) {
	try {
		URL fullUrl = URI.create(url).toURL();

		String path = fullUrl.getPath();
		if (path.length() > 0) {
			StringTokenizer pathTokenizer = new StringTokenizer(path, "/");
			this.contextRoot = pathTokenizer.nextToken();
//			this.appliUrl = new URL(fullUrl.getProtocol(), fullUrl.getHost(), fullUrl.getPort(), "/"+this.contextRoot);
			this.appliUrl = new URI(fullUrl.getProtocol(), null /*userInfo*/, fullUrl.getHost(), fullUrl.getPort(), "/"+this.contextRoot, null /*query*/, null /*fragment*/).toURL();
			this.location = this.appliUrl.toExternalForm();
			if (pathTokenizer.countTokens() > 0) {
				debugPrintln("Info: path for application '"+this.location+"' contains several segments. Context root ("+this.contextRoot+") was initialized with first one.");
			}

		} else {
			this.appliUrl = fullUrl;
			this.location = url;
		}
		this.server = this.appliUrl.getAuthority();
    }
	catch (MalformedURLException | URISyntaxException e) {
		throw new ScenarioFailedError(e);
    }
}

protected Application(final String url, final String serverUrl) {
	this(url);
	this.server = serverUrl;
}

protected Application(final URL url) {
	this.contextRoot = null;
	this.appliUrl = url;
	this.location = url.toExternalForm();
	this.server = url.getAuthority();
}

@Override
public boolean equals(final Object obj) {
	if (obj instanceof Application) {
		Application application = (Application) obj;
		return this.location.equals(application.location);
	}
	return super.equals(obj);
}

/**
 * Return the context root of the application.
 *
 * @return The context root as a {@link String}.
 */
public String getContextRoot() {
	return this.contextRoot;
}

/**
 * Return the host of the machine on which the current application is installed.
 * <p>
 * The returned string is the hostname in the expected application location URL format:<br>
 * <i>&lt;protocol&gt;</i>://<b><i>&lt;hostname&gt;</i></b>:<i>&lt;port&gt;</i>
 * </p>
 * @return The host name as a {@link String}
 */
public String getHost() {
	return this.appliUrl.getHost();
}

/**
 * Return the host URL of the machine on which the current application is installed.
 * <p>
 * The returned string is kind of <i>&lt;protocol&gt;</i>://<i>&lt;hostname&gt;</i>:<i>&lt;port&gt;</i>
 * e.g. <pre>https://fit-vm12-96.rtp.raleigh.ibm.com:9443</pre>
 * </p>
 * @return The host url as a {@link String}
 */
public String getHostUrl() {
	return this.server+":"+this.appliUrl.getPort();
}

/**
 * Returns the license that the application uses.
 *
 * @return The application license as a {@link String}.
 */
public String getLicense() {
	return this.license;
}

/**
 * Returns the application licenses.
 *
 * @return The application licenses as an array of {@link String}.
 */
public String[] getLicenses() {
	return NO_LICENSE;
}

/**
 * The application location.
 *
 * @return The location as a {@link String}.
 */
public String getLocation() {
	return this.location;
}

/**
 * Returns the application name.
 *
 * @return The name as a {@link String}.
 */
public String getName() {
	if (this.name == null) {
		String className = getClassSimpleName(getClass());
		this.name = className.substring(0, className.indexOf("App")).toUpperCase();
	}
	return this.name;
}

/**
 * Return the modified page URL if necessary.
 * <p>
 * Default is not to modify the page url.
 * </p>
 * @return The page URL as a {@link String}.
 */
public String getPageUrl(final String pageUrl) {
	return pageUrl;
}

/**
 * Return the port number of the URL of the machine on which the current application is installed.
 *
 * @return The port number.
 */
public int getPort() {
	return this.appliUrl.getPort();
}

/**
 * Returns the application product name.
 *
 * @return The application product name as a {@link String}.
 */
public String getProductName() {
	throw new ScenarioFailedError(this+" has no associated product.");
}

/**
 * Return the protocol name of the URL of the machine on which the current application is installed.
 *
 * @return The protocol name as {@link String}.
 */
public String getProtocol() {
	return this.appliUrl.getProtocol();
}

/**
 * Returns the application server.
 *
 * @return The application server as a {@link String}.
 */
public String getServer() {
	return this.server;
}

/**
 * Returns the application suffix.
 * <p>
 * Default is no suffix.
 * </p>
 * @return The application suffix as a {@link String}
 */
public String getSuffix() {
	return EMPTY_STRING;
}

/**
 * Returns the application title.
 *
 * @return The application title as a {@link String}.
 */
public String getTitle() {
	return toString();
}

///**
// * Returns the application type.
// *
// * @return The application type as a {@link String}.
// */
//public String getType() {
//	return getTitle();
//}

/**
 * Returns the type suffix.
 * <p>
 * Default is no suffix.
 * </p>
 * @return The type suffix as a {@link String}
 */
public String getTypeSuffix() {
	return EMPTY_STRING;
}

public User getUser() {
	return this.user;
}

@Override
public int hashCode() {
	return this.location.hashCode();
}

/**
 * Tells whether the given URL address matches the current application or not.
 *
 * @param url The URL address
 * @return <code>true</code> if the address belongs to the current application,
 * <code>false</code> otherwise
 */
boolean isApplicationFor(final String url) {
	return url.toLowerCase().startsWith(this.location.toLowerCase());
}

/**
 * Login the given user to the application.
 *
 * @param newUser The new user which would be connected to the application
 * @return <code>true</code> if the user was changed on the current application,
 * <code>false</code> otherwise.
 */
public boolean login(final User newUser) {
	if (newUser == null) {
		throw new IllegalArgumentException("Cannot login when no user is specified.");
	}
	if (DEBUG) debugPrintln("		+ Log in user '"+newUser+"' to current "+this);
	if (this.user != null && this.user.equals(newUser)) {
		if (DEBUG) debugPrintln("		  -> nothing was done as there user was already logged in.");
		return false;
	}
	if (this.user != null) {
		if (DEBUG) debugPrintln("		  -> user '"+this.user+"' was initially logged in.");
	}
	this.user = newUser;
	return true;
}

/**
 * Logout the current user from the application.
 *
 * @return <code>true</code> if the user was logged out from the current
 * application, <code>false</code> otherwise.
 */
public boolean logout() {
	if (this.user != null) {
		if (DEBUG) debugPrintln("		+ Log out user "+this.user.getId()+" for "+this);
		this.user = null;
		return true;
	}
	return false;
}

/**
 * Tells whether the current application would need login for the given user.
 *
 * @param newUser The new user which would be connected to the application
 * @return <code>true</code> if the user implied a login operation if it would
 * connect to the application, <code>false</code> otherwise
 */
public boolean needLogin(final User newUser) {
	boolean needLogin = this.user == null || !this.user.equals(newUser);
	if (DEBUG) {
		if (needLogin) {
			debugPrint("		+ "+this+" need login as ");
			if (this.user == null) {
				debugPrintln("no user is set.");
			} else {
				debugPrintln("current user "+this.user.getId()+" would change to "+newUser.getId());
			}
		}
	}
	return needLogin;
}

/**
 * Set the application name.
 *
 * @noreference Should not be used outside the framework
 * @param name The name to be set for the current application
 */
public void setName(final String name) {
	this.name = name;
}

@Override
public final String toString() {
	StringBuilder builder = new StringBuilder(getName()).append(" Application (location=").append(this.location);
	if (this.user == null) {
		builder.append(", no user logged in)");
	} else {
		builder.append(", user logged in: ").append(this.user).append(")");
	}
	return builder.toString();
}

}
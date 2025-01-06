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
package itest.cloud.util;

/**
 * A class to manage loading of other classes in different plug-ins/layers.
 */
public class CustomClassLoader {

/**
 * Answers a Class object which represents the class named by the argument. The name
 * should be the name of a class as described in the class definition of java.lang.Class,
 * however Classes representing base types can not be found using this method.
 * <p>
 * This method utilizes both the default Java class loader and individual plug-in/bundle
 * specific class loaders. When a test is run as a Java application (JUnit test), loading
 * classes via the default Java class loader is adequate. However, when a test is run as
 * an Eclipse application (JUnit plug-in test), the default Java class loader fails to
 * load classes in other plug-ins/bundles. Therefore, the class loader of each individual
 * plug-in/bundle must be used for this purpose.
 * </p>
 *
 * @param className	The name of the non-base type class to find
 * @return the named Class
 * @throws ClassNotFoundException If the class could not be found
 */
public static Class< ? > forName(final String className) throws ClassNotFoundException {
	// When a test is run as a Java application (JUnit test), loading
	// classes via the default Java class loader is adequate.
	return Class.forName(className);
}
}

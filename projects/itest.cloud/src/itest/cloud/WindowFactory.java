/*********************************************************************
 * Copyright (c) 2013, 2022 IBM Corporation and others.
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
package itest.cloud;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.openqa.selenium.By;

import itest.cloud.factories.Factory;
import itest.cloud.pages.Page;
import itest.cloud.pages.dialogs.AbstractWindow;

/**
 * Factory to create instances of {@link AbstractWindow}.
 */
public class WindowFactory extends Factory {

private WindowFactory() {
    super(null);
}

/**
 * Create an instance of the given web window class located in the given page.
 * <p>
 * When using this factory method, the framework assumes that the given
 * class has a constructor with a single {@link Page} or one of its direct
 * subclass parameter.
 * </p>
 * @param page The page from which the window will belong to
 * @param windowClass The framework class of the window
 * @return The instance of the given window class
 * @throws Exception Thrown if typically the expected class constructor does
 * not exist.
 */
public static <W extends AbstractWindow> W createInstance(final Page page, final Class<W> windowClass) throws Exception {
	return createInstance(page, null, windowClass, (String[]) null);
}

/**
 * Create an instance of the given web window class located in the given page.
 * <p>
 * When using this factory method, the framework assumes that the given
 * class has a constructor with a single {@link Page} or one of its direct
 * subclass parameter.
 * </p>
 * @param page The page from which the window will belong to
 * @param windowClass The framework class of the window
 * @param data Additional data provided when creating the instance as a list
 * of strings
 * @return The instance of the given window class
 * @throws Exception Thrown if typically the expected class constructor does
 * not exist.
 */
public static <W extends AbstractWindow> W createInstance(final Page page, final Class<W> windowClass, final String... data) throws Exception {
	return createInstance(page, null, windowClass, data);
}

/**
 * Create an instance of the given web window class located in the given page.
 * <p>
 * When using this factory method, the framework assumes that the given
 * class has a constructor with following parameters:
 * <ul>
 * <li>{@link Page} or one of its direct subclass</li>
 * <li>{@link By}</li>
 * </ul>
 * </p>
 * @param page The page from which the window will belong to
 * @param locator The mechanism to find the web window element when opened
 * @param windowClass The framework class of the window
 * @return The instance of the given window class
 * @throws Exception Thrown if typically the expected class constructor does
 * not exist.
 */
public static <W extends AbstractWindow> W createInstance(final Page page, final By locator, final Class<W> windowClass) throws Exception {
	return createInstance(page, locator, windowClass, (String[]) null);
}

/**
 * Create an instance of the given web window class located in the given page.
 * <p>
 * When using this factory method, the framework assumes that the given
 * class has a constructor with following parameters:
 * <ul>
 * <li>{@link Page} or one of its direct subclass</li>
 * <li>{@link By}</li>
 * <li>{@link String}...</li>
 * </ul>
 * </p>
 * @param page The page from which the window will belong to
 * @param locator The mechanism to find the web window element when opened
 * @param windowClass The framework class of the window
 * @param data Additional data provided when creating the instance as a list
 * of strings
 * @return The instance of the given window class
 * @throws Exception Thrown if typically the expected class constructor does
 * not exist.
 */
@SuppressWarnings("unchecked")
public static <W extends AbstractWindow> W createInstance(final Page page, final By locator, final Class<W> windowClass, final String... data) throws Exception {

	// Start from the first abstract class
	Class<? extends Page> pageClass = page.getClass();
	while ((pageClass.getModifiers() & Modifier.ABSTRACT) == 0) {
		pageClass = (Class<? extends Page>) pageClass.getSuperclass();
	}

	// Loop until found the constructor on the right subclass of WebPage
	Exception exception = null;
	while (pageClass != null) {
		try {
			// Use default locator constructors
			if (locator == null) {

				// Use no data constructor
				if (data == null || data.length == 0) {
					Constructor<W> constructor = windowClass.getConstructor(pageClass);
					return constructor.newInstance(page);
				}

				// Use data constructor
				Constructor<W> constructor = windowClass.getConstructor(pageClass, String[].class);
				return constructor.newInstance(page, data);
			}

			// Use locator constructor with no data
			if (data == null || data.length == 0) {
				Constructor<W> constructor = windowClass.getConstructor(pageClass, By.class);
				return constructor.newInstance(page, locator);
			}

			// Use locator constructor with data
			Constructor<W> constructor = windowClass.getConstructor(pageClass, By.class, String[].class);
			return constructor.newInstance(page, locator, data);
		}
		catch (Exception ex) {
			if (exception == null) {
				exception = ex;
			}
		}
		pageClass = (Class< ? extends Page>) pageClass.getSuperclass();
	}

	// No constructor were found, give up
	if (exception != null) {
		throw exception;
	}
	throw new Exception("Cannot create instance of "+windowClass.getName()+" web menu.");
}
}

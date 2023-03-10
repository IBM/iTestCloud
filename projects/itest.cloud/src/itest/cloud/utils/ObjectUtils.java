/*********************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
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
package itest.cloud.utils;

import java.util.regex.Pattern;

import junit.framework.AssertionFailedError;

/**
 * Utility class consisting of {@code static} utility methods for operating on objects.
 * <p>
 * This class contains following API methods:
 * <ul>
 * <li>{@link #matches(Pattern, String)}: Attempts to match the given text against the pattern.</li>
 * <li>{@link #requireNonNull(Object, String)}: Check that the given object reference is not {@code null}
 * and throw a customized {@link NullPointerException} if it is</li>
 * <li>{@link #requireNonNull(Object, AssertionFailedError)}: Check that the given object reference is not
 * {@code null} and throw a customized error that is or extends {@link AssertionFailedError} if it is.</li>
 * </ul>
 * </p>
 */
public final class ObjectUtils {

/**
 * Attempts to match the given text against the pattern.
 *
 * @param pattern the pattern to compare the text against as {@link Pattern}.
 * @param text the text to compare.
 *
 * @return  <tt>true</tt> if the given text matches the pattern or <code>false</code> otherwise.
 *
 * @throws NullPointerException if the given pattern is <code>null</code>.
 */
public static boolean matches(final Pattern pattern, final String text) {
	return (text != null) && pattern.matcher(text).matches();
}

/**
 * Check that the given object reference is not {@code null} and throw a customized {@link NullPointerException}
 * if it is. This method is mainly used for parameter validation in methods and constructors with multiple
 * parameters, as demonstrated below:
 *
 * <pre>
 * public Foo(Bar bar, Baz baz) {
 *     this.bar = ObjectUtils.requireNonNull(bar, "bar must not be null");
 *     this.baz = ObjectUtils.requireNonNull(baz, "baz must not be null");
 * }
 * </pre>
 *
 * @param object The object reference to check for nullity.
 * @param message The detail message to be used in the event that a {@code NullPointerException} is thrown.
 * @param <T> The type of the object reference.
 *
 * @return {@code object} if not {@code null}.
 *
 * @throws NullPointerException if {@code object} is {@code null}.
 */
public static <T> T requireNonNull(final T object, final String message) {
    if (object == null) throw new NullPointerException(message);
    return object;
}

/**
 * Check that the given object reference is not {@code null} and throw a customized error that is or extends
 * {@link AssertionFailedError} if it is. This method is mainly used for parameter validation in methods and
 * constructors with multiple parameters, as demonstrated below:
 *
 * <pre>
 * public Foo(Bar bar, Baz baz) {
 *     this.bar = ObjectUtils.requireNonNull(bar, new AssertionFailedError("bar must not be null."));
 *     this.baz = ObjectUtils.requireNonNull(baz, new AssertionFailedError("baz must not be null."));
 * }
 * </pre>
 *
 * @param object The object reference to check for nullity.
 * @param error The customized error containing a detail message that is or extends {@code AssertionFailedError}
 * to be thrown in the event that {@code object} is {@code null}.
 * @param <T> The type of the reference.
 * @param <U> The type of the customized error that is or extends {@code AssertionFailedError}.
 *
 * @return {@code object} if not {@code null}.
 *
 * @throws U is or extends {@link AssertionFailedError} if {@code object} is {@code null}.
 */
public static <T, U extends AssertionFailedError> T requireNonNull(final T object, final U error) {
    if (object == null) throw error;
    return object;
}
}
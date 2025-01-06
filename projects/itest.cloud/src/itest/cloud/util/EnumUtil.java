/*********************************************************************
 * Copyright (c) 2017, 2022 IBM Corporation and others.
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

import java.security.SecureRandom;

/**
 * Utilities to perform various operations on enums.
 * <p>
 * </p>
 */
public class EnumUtil {

	private static final SecureRandom RANDOM = new SecureRandom();

/**
 * Return a random value of a given enum.
 *
 * @param clazz The class of the enum.
 *
 * @return A random value of the given enum.
 */
public static <T extends Enum<?>> T randomEnum(final Class<T> clazz){
    int x = RANDOM.nextInt(clazz.getEnumConstants().length);
    return clazz.getEnumConstants()[x];
}
}

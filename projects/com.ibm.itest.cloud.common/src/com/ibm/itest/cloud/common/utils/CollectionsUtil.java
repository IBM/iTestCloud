/*******************************************************************************
 * Licensed Materials - Property of IBM
 * © Copyright IBM Corporation 2022. All Rights Reserved.
 * 
 * Note to U.S. Government Users Restricted Rights:
 * Use, duplication or disclosure restricted by GSA ADP Schedule
 * Contract with IBM Corp. 
 *******************************************************************************/
/*********************************************************************
 * Copyright (c) 2014, 2022 IBM Corporation and others.
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
package com.ibm.itest.cloud.common.utils;

import java.util.*;

/**
 * Class to provide utilities around {@link Collection} and array that neither
 * {@link Collections} nor {@link Arrays} currently offers.
 */
public class CollectionsUtil {

/**
 * Return the given array as a list of same objects.
 *
 * @param array The array to build as a list
 * @return The list as as {@link List}
 */
public static <T> List<T> getListFromArray(final T[] array) {
	return Arrays.asList(array);
}

}

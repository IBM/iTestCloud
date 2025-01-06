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
package itest.cloud.util;

/**
 * Enumeration to define a list of comparison criteria for matching
 * two strings.
 */
public enum StringComparisonCriterion {

	EQUALS {
		/**
		 * Compare the two given strings for equality.<br>
		 * <p>The comparison is done as follows: <b>text1.equals(text2)</b></p>
		 *
		 * @param text1 First string
		 * @param text2 Second string
		 * @return <b>true</b> if the 2 strings are equal or <b>false</b> otherwise.
		 */
		@Override
		public boolean compare(final String text1, final String text2) {
			return text1.equals(text2);
		}

		@Override
		public String toString() {
			return "equals";
		}
	},

	ENDSWITH {
		/**
		 * Compare if the first string ends with the second string.<br>
		 * <p>The comparison is done as follows: <b>text1.endsWith(text2)</b></p>
		 *
		 * @param text1 First string
		 * @param text2 Second string
		 * @return <b>true</b> if the first string ends with the second string or <b>false</b> otherwise.
		 */
		@Override
		public boolean compare(final String text1, final String text2) {
			return text1.endsWith(text2);
		}

		@Override
		public String toString() {
			return "ends with";
		}
	},

	STARTSWITH {
		/**
		 * Compare if the first string starts with the second string.<br>
		 * <p>The comparison is done as follows: <b>text1.startsWith(text2)</b></p>
		 *
		 * @param text1 First string
		 * @param text2 Second string
		 * @return <b>true</b> if the first string starts with the second string or <b>false</b> otherwise.
		 */
		@Override
		public boolean compare(final String text1, final String text2) {
			return text1.startsWith(text2);
		}

		@Override
		public String toString() {
			return "starts with";
		}
	},

	CONTAINS {
		/**
		 * Compare if the first string contains the second string.<br>
		 * <p>The comparison is done as follows: <b>text1.contains(text2)</b></p>
		 *
		 * @param text1 First string
		 * @param text2 Second string
		 * @return <b>true</b> if the first string contains the second string or <b>false</b> otherwise.
		 */
		@Override
		public boolean compare(final String text1, final String text2) {
			return text1.contains(text2);
		}

		@Override
		public String toString() {
			return "contains";
		}
	};

	/**
	 * Compare the two given strings in the corresponding criterion.<br>
	 *
	 * @param text1 First string
	 * @param text2 Second string
	 * @return <b>true</b> if the 2 strings match in the corresponding criterion or <b>false</b> otherwise.
	 */
	public abstract boolean compare(final String text1, final String text2);
}

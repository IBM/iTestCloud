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
package itest.cloud.pages.elements;

import java.util.List;
import java.util.regex.Pattern;

import itest.cloud.scenario.errors.ScenarioFailedError;

/**
 * Interface for a table element.
 * <p>
 * Following methods are available on such element:
 * <ul>
 * <li>{@link #applySortMode(Pattern, SortMode)}: Apply the given sort mode to the given column.</li>
 * <li>{@link #getColumnHeaders()}: Return the list of displayed columns.</li>
 * <li>{@link #getColumnSortMode(Pattern)}: Return the sort mode of the column macthing the given name.</li>
 *</ul>
 * </p>
 */
public interface ITableElement {
	/**
	 * Enum representing the 3 different sorting states: <b>ASCENDING</b>, <b>DESCENDING</b> and <b>NONE</b> to return to original sorting.
	 */
	public enum SortMode {
		/**
		 * Mode used to sort a table column in the ascending order.
		 */
		ASCENDING("ASC"),
		/**
		 * Mode used to sort a table column in the descending order.
		 */
		DESCENDING("DESC"),
		/**
		 * Mode used to specify that a table column in not sorted.
		 */
		NONE("None");

		/**
		 * Get the enumeration value for the given attribute value.
		 *
		 * @param attributeValue The attribute value
		 * @return The corresponding enum value
		 * @throws ScenarioFailedError If the given attribute value does not match any enumeration value
		 */
		public static SortMode fromAttribute(final String attributeValue) {
	        for (SortMode mode: values()) {
	        	if (mode.attribute.equals(attributeValue)) {
	        		return mode;
	        	}
	        }
	        throw new ScenarioFailedError("Attribute value '"+attributeValue+"' is not a known value for SortMode enumeration!");
        }

		/**
		 * Get the enumeration value for the given text.
		 *
		 * @param text The text of the enumeration
		 * @return The corresponding enum value
		 * @throws ScenarioFailedError If the given text does not match any enumeration label
		 */
		public static SortMode fromText(final String text) {
	        for (SortMode mode: values()) {
	        	if (mode.label.equals(text)) {
	        		return mode;
	        	}
	        }
	        throw new ScenarioFailedError("Text '"+text+"' is not a known label for SortMode enumeration!");
        }

		/* Fields */
		private final String label;
		private final String attribute;

		SortMode(final String value) {
			this(value,value.toLowerCase());
		}

		SortMode(final String value, final String attrib) {
			this.label = value;
			this.attribute = attrib;
		}

		@Override
		public String toString() {
			return this.label;
		}
	}

/**
 * Apply the given sort mode to the given column.
 *
 * @param pattern The pattern matching the name of the column to be found as {@link Pattern}.
 * @param mode The sort mode to apply
 * @throws ScenarioFailedError If the column is not sortable
 */
void applySortMode(final Pattern pattern, final SortMode mode) throws ScenarioFailedError;

/**
 * Return the list of displayed columns.
 *
 * @return The column names list as a {@link List} of {@link String}.
 */
List<String> getColumnHeaders();

/**
 * Return the sort mode of the column macthing the given name.
 *
 * @param pattern The pattern matching the name of the column to be found as {@link Pattern}.
 * @return The column names list as a {@link List} of {@link String} or <code>null</code>
 * if the column is not sortable in the current table
 * @throws ScenarioFailedError If there's no column in the current table matching the given name
 */
SortMode getColumnSortMode(final Pattern pattern) throws ScenarioFailedError;
/**
 * Check if the given column is displayed.
 *
 * @param pattern The pattern matching the name of the column to be found as {@link Pattern}.
 * @return <code>true</code> if the coulmn is displayed, <code>false</code> otherwise
 */
public boolean isColumnDisplayed(final Pattern pattern);
}

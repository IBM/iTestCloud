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
package com.ibm.itest.cloud.common.pages.elements;

import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.DEBUG;
import static com.ibm.itest.cloud.common.tests.scenario.ScenarioUtils.debugPrintln;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.openqa.selenium.*;

import com.ibm.itest.cloud.common.pages.WebPage;
import com.ibm.itest.cloud.common.pages.frames.WebBrowserFrame;
import com.ibm.itest.cloud.common.tests.scenario.errors.ScenarioFailedError;
import com.ibm.itest.cloud.common.tests.utils.StringComparisonCriterion;

/**
 * Class to handle web element with <code>table</code> tag name.
 */
public abstract class AbstractTableElement extends WebElementWrapper implements TableElement {

	/**
	 * This class represents a error where the sort mode of a table column not
	 * changing after a corresponding sorting operation has completed.
	 */
	public class ColumnSortUnchangedError extends ScenarioFailedError {
		public ColumnSortUnchangedError(final Pattern pattern, final SortMode currentMode, final SortMode newMode) {
			super("The sort mode for column matching pattern '" + pattern + "' did not change from " + currentMode + " to " + newMode);
		}
		public ColumnSortUnchangedError(final String column, final SortMode currentMode, final SortMode newMode) {
			super("The sort mode for column '" + column + "' did not change from " + currentMode + " to " + newMode);
		}
	}

	/* Fields */
	// Be cautious with this field has to be used only inside a method to avoid getting this web element multiple times
	// When using the first time in a method always assume it's either null or stale and reinitialize first using getHeaderElement method...
	protected WebBrowserElement headerElement;

public AbstractTableElement(final WebElementWrapper parent, final By selectBy) {
	super(parent, selectBy);
}

public AbstractTableElement(final WebElementWrapper parent, final WebBrowserElement element) {
	super(parent, element);
}

public AbstractTableElement(final WebPage page) {
	super(page);
}

public AbstractTableElement(final WebPage page, final By findBy) {
	super(page, findBy);
}

public AbstractTableElement(final WebPage page, final By findBy, final WebBrowserFrame frame) {
	super(page, findBy, frame);
}

public AbstractTableElement(final WebPage page, final WebBrowserElement element) {
	super(page, element);
}

public AbstractTableElement(final WebPage page, final WebBrowserElement element, final WebBrowserFrame frame) {
	super(page, element, frame);
}

public AbstractTableElement(final WebPage page, final WebBrowserFrame frame) {
	super(page, frame);
}

@Override
public void applySortMode(final Pattern pattern, final SortMode mode) throws ScenarioFailedError {
	if (DEBUG) debugPrintln("		+ Apply sort mode '" + mode + "' to column matching pattern '" + pattern + "'.");

	// Check if it's necessary to set mode
	SortMode currentMode = getColumnSortMode(pattern);
	if (currentMode == mode) {
		if (DEBUG) debugPrintln("		  -> sort mode was already active on the column, do nothing...");
		return;
	}

	// Click on header element until it's mode matches the expected one
	this.headerElement.makeVisible();
	while (true) {
		List<WebBrowserElement> childElements = this.headerElement.getChildren();
		boolean foundDiv = false;
		for (WebBrowserElement childElement: childElements) {
			if (childElement.getTagName().equals("div")) {
				foundDiv = true;
				childElement.click();
				break;
			}
		}
		if (!foundDiv)
			this.headerElement.click();
		waitForTableToRefresh();
		SortMode newMode = getSortMode(this.headerElement);
		if (newMode == mode) {
			// We found the expected mode, leave now
			break;
		}
		if (newMode == currentMode) {
			throw new ColumnSortUnchangedError(pattern, currentMode, mode);
		}
	}
}

/**
 * Return the specified table cell element.
 *
 * @param row The row of the specified cell.
 * @param column The column of the specified cell.
 *
 * @return The specified table cell element as {@link WebBrowserElement}.
 */
public WebBrowserElement getCellElement(final int row, final int column) {
	return getCellElementsForRow(row).get(column);
}

private List <WebBrowserElement> getCellElementsForRow(final int row) {
	List<WebBrowserElement> rowElements = getRowElements();
	return rowElements.get(row).waitForElements(
		By.xpath(".//td | .//div[starts-with(@class,'ReactVirtualized__Table__rowColumn')]"), tinyTimeout(), false /*displayed*/);
}

/**
 * Return the image source from the specified table cell.
 *
 * @param row The row of the specified cell.
 * @param column The column of the specified cell.
 *
 * @return The image source from the specified table cell if it exists or
 * <code>null</code> otherwise.
 */
public String getCellImageSource(final int row, final int column) {
	WebBrowserElement imageElement = getCellElement(row, column).findElement(By.xpath(".//img"));
	if (imageElement == null)
		return null;
	return imageElement.getAttribute("src");
}

/**
 * Return the text in the specified table cell.
 *
 * @param row The row of the specified cell.
 * @param column The column of the specified cell.
 *
 * @return The text in the specified table cell as {@link String}.
 */
public String getCellText(final int row, final int column) {
	return getCellText(getCellElement(row, column));
}

private String getCellText(final WebBrowserElement cellElement) {
	List<WebElement> buttonElements =
		cellElement.findElements(By.xpath(".//button[contains(@class,'select__button')]"));

	String cellText = buttonElements.isEmpty() ? cellElement.getText() : buttonElements.get(0).getText();
	return cellText != null ? cellText : "";
}

/**
 * Return all the cell text values for a specified table row.
 *
 * @param row The table row to be accessed.
 *
 * @return A String List containing the text values for each cell in the table row.
 */
public List <String> getCellTextForRow(final int row) {
	List<WebBrowserElement> cellElements = getCellElementsForRow(row);
	List<String> cellsValues = new ArrayList<String>(cellElements.size());
	for (WebBrowserElement cElement: cellElements) {
		cellsValues.add(getCellText(cElement));
	}

	return cellsValues;
}

@Override
public List<String> getColumnHeaders() {
	List<WebBrowserElement> headersElement = getHeaderElements();
	List<String> headers = new ArrayList<String>(headersElement.size());
	for (WebBrowserElement hElement: headersElement) {
		headers.add(hElement.getText());
	}
	return headers;
}

@Override
public SortMode getColumnSortMode(final Pattern pattern) throws ScenarioFailedError {
	if (getHeaderElement(pattern) == null) {
		throw new ScenarioFailedError("There's no column matching pattern '" + pattern + "' in the current table.");
	}
	return getSortMode(this.headerElement);
}

/**
 * Return the full column header that matches the given column name.
 * <p>
 * Note that this method is setting {@link #headerElement} field.
 * </p>
 * @param pattern The pattern matching the name of the column to be found as {@link Pattern}.
 * @return The full column header text as a {@link String}
 * @throws ScenarioFailedError if there's no column with the given name in the current table
 */
public String getColumnHeader(final Pattern pattern) {
	if (getHeaderElement(pattern) == null) {
		throw new ScenarioFailedError("There's no column matching pattern '" + pattern + "' in the current table.");
	}
	return this.headerElement.getText();
}

/**
 * Return the header web element which text is matching the given column name.
 * <p>
 * Note that this method is setting {@link #headerElement} field.
 * </p>
 * @param pattern The pattern matching the name of the column to be found as {@link Pattern}.
 * @return The header element as a {@link WebBrowserElement} or <code>null</code>
 * if there's no column with the given name in the current table
 */
protected WebBrowserElement getHeaderElement(final Pattern pattern) {
	List<WebBrowserElement> headersElement = getHeaderElements();
	for (WebBrowserElement hElement: headersElement) {
		if (pattern.matcher(hElement.getText()).matches()) {
			return this.headerElement = hElement;
		}
	}
	return null;
}

/**
 * Return the list of header web elements.
 * <p>
 * By default these are web elements with <code>td</code> tag name and having
 * the <code>@role='columnheader'</code> attribute.
 * </p><p>
 * Subclass might want to override this method if the way to get these elements
 * is different.
 * </p>
 * @return The web elements as a {@link List} of {@link WebBrowserElement}.
 */
protected List<WebBrowserElement> getHeaderElements() {
	return this.element.waitForElements(By.xpath(".//th | .//div[starts-with(@class,'ReactVirtualized__Table__headerColumn')]"));
}

/**
 * Return the number of rows in the table.
 *
 * @return The number of rows in the table.
 */
public int getRowCount() {
	return getRowElements().size();
}

private List<WebBrowserElement> getRowElements() {
	return this.element.waitForElements(By.xpath(".//tbody/tr | .//div[starts-with(@class,'ReactVirtualized__Table__row')][@role='row']"), tinyTimeout());
}

/**
 * Return the name of the column which has sorting activated.
 *
 * @return The column name as a {@link String} or <code>null</code>
 * if there's no sorted column in the current table
 */
public String getSortedColumn() {
	List<WebBrowserElement> headersElement = getHeaderElements();
	for (WebBrowserElement hElement: headersElement) {
		if (getSortMode(hElement) != SortMode.NoSort) {
			return hElement.getText();
		}
	}
	return null;
}

/**
 * Return the sorting state of the given column header element.
 * <p>
 * By default a column is sorted if its header element has the <code>aria-sort</code>
 * attribute equals to one of the attribute value of
 * {@link com.ibm.itest.cloud.common.pages.elements.TableElement.SortMode} enumeration.
 * </p><p>
 * Subclass might want to override this behavior if the sorting flag is specifically
 * managed in the corresponding table element.
 * </p>
 * @param hElement The header element to get sorting mode
 * @return The sorting mode as a {@link com.ibm.itest.cloud.common.pages.elements.TableElement.SortMode}
 * value or <code>null</code>
 * if the column is not sortable
 */
protected SortMode getSortMode(final WebBrowserElement hElement) {
	String sortAttribute = "None";
	WebBrowserElement iconElement =
		hElement.waitForElement(By.xpath(".//button | .//span[starts-with(@class,'DataGrid') and contains(@class,'__activeSort')]/*[name()='svg'][starts-with(@class,'ReactVirtualized__Table__sortableHeaderIcon')]"), tinyTimeout());

	if (iconElement != null) {
		String iconClass = iconElement.getAttribute("class");
		// Classes look like these:
		// 	ReactVirtualized__Table__sortableHeaderIcon ReactVirtualized__Table__sortableHeaderIcon--ASC
		// 	ReactVirtualized__Table__sortableHeaderIcon ReactVirtualized__Table__sortableHeaderIcon--DESC
		//  customDataTable__colWidth30___1cGIz bx--table-sort bx--table-sort--active
		//  customDataTable__colWidth30___1cGIz bx--table-sort bx--table-sort--active bx--table-sort--ascending
		sortAttribute = iconClass.substring(iconClass.lastIndexOf('-') + 1).toUpperCase();
		if (sortAttribute.equals("ACTIVE"))
			return SortMode.Descending;
		sortAttribute = sortAttribute.substring(0, sortAttribute.indexOf('C') + 1);
	}
	return SortMode.fromText(sortAttribute);
}

@Override
public boolean isColumnDisplayed(final Pattern pattern) {
	for (String columnHeader : getColumnHeaders()) {
		if(pattern.matcher(columnHeader).matches()) return true;
	}

	return false;
}

/**
 * Search the table for a specific pattern.
 *
 * @param pattern The pattern to search for.
 * @param fail Specify whether to fail if a possible match is not found.
 * @param columns List of columns to search.
 *
 * @return Zero-based row number or -1 if the target text was not found and asked not to fail.
 */
public int search(final Pattern pattern, final boolean fail, final int... columns) {
	for (int row = 0; row < getRowCount(); row++) {
		List <String> rowCellTextList = getCellTextForRow(row);

		for (int column: columns) {
			String cellText = rowCellTextList.get(column);

			if(pattern.matcher(cellText).matches()) {
				return row;
			}
		}
	}

	if (fail) throw new ScenarioFailedError("Pattern '" + pattern + "' was not found in the table");

	return -1;
}

/**
 * Search the table for a specific text.
 *
 * @param searchText The text to search for.
 * @param useCompleteMatch If true, the entire cell contents much match the target text.
 * If false, the cell contents must contain the target text.
 * @param ignoreCase Specifies whether to ignore the case while searching for a possible match.
 * @param fail Specify whether to fail if a possible match is not found.
 * @param columns List of columns to search.
 *
 * @return Zero-based row number or -1 if the target text was not found and asked not to fail.
 */
public int search(final String searchText, final boolean useCompleteMatch, final boolean ignoreCase, final boolean fail, final int... columns) {
	String quotedPatternText = Pattern.quote(searchText);
	String searchPatternText = useCompleteMatch ? quotedPatternText : (".*" + quotedPatternText + ".*");
	Pattern searchPattern = Pattern.compile(searchPatternText, ignoreCase ? Pattern.CASE_INSENSITIVE : 0);

	return search(searchPattern, fail, columns);
}

/**
 * Search the table for a specific text. This method is faster than the search methods that return a row number.
 *
 * @param searchText The text to search for.
 * @param fail Specify whether to fail if a match is not found.
 * @param column Column to search.
 *
 * @return A list of cell elements as {@link List} of {@link WebBrowserElement} of the cells in the specified column that
 * contain the specified text. The list may be empty if the target text was not found and asked not to fail.
 */
public List<WebBrowserElement> search(final String searchText, final boolean fail, final int column) {
	return search(searchText, StringComparisonCriterion.EQUALS, fail, column);
}

/**
 * Search the table for a specific text. This method is faster than the search methods that return a row number.
 *
 * @param searchText The text to search for.
 * @param comparison The comparison used to match the searchText to the contents of the specified column as {@link StringComparisonCriterion}.
 * @param fail Specify whether to fail if a match is not found.
 * @param column Column to search.
 *
 * @return A list of cell elements as {@link List} of {@link WebBrowserElement} of the cells in the specified column that
 * contain the specified text. The list may be empty if the target text was not found and asked not to fail.
 */
public List<WebBrowserElement> search(final String searchText, final StringComparisonCriterion comparison, final boolean fail, final int column) {
	String xpathExpression = "[" + (column + 1) + "]";
	switch (comparison) {
	case CONTAINS:
		xpathExpression += "[contains(.,\"" + searchText + "\")]";
		break;
	case ENDSWITH:
		xpathExpression += "[ends-with(.,\"" + searchText + "\")]";
		break;
	case EQUALS:
		xpathExpression += "[.=\"" + searchText + "\"]";
		break;
	case STARTSWITH:
		xpathExpression += "[starts-with(.,\"" + searchText + "\")]";
		break;
	}
	xpathExpression = ".//tbody/tr/td" + xpathExpression + " | .//*[contains(@class,'ReactVirtualized__Table__row')]/*" + xpathExpression;
	return this.browser.waitForElements(this.element, By.xpath(xpathExpression), fail, tinyTimeout(), true/*displayed*/);
}

/**
 * Is the specified row selected? This works only for tables where the first column is a selection box.
 *
 * @param row The table row number (0 based) to check for selection.
 *
 * @return Boolean indicating whether the specified row is selected.
 */
public boolean isSelected(final int row) {
	return getCellElement(row, 0 /*column*/).findElement(By.xpath(".//input")).isSelected();
}

/**
 * Select or unselect the specified row. This works only for tables where the first column is a selection box.
 *
 * @param row The number (0 based) of the table row to select or unselect.
 * @param selectIt Indicates whether the row should be selected or unselected.
 */
public void select(final int row, final boolean selectIt) {
	WebBrowserElement cellElement = getCellElement(row, 0).findElement(By.xpath(".//input"));

	if (cellElement.isSelected() != selectIt) {
		// Click on the cell element.
		// At times, the WebBrowserElement.click() method may not bring the open element into view by scrolling
		// the page appropriately. Therefore, use JavaScript to perform the click on the cell element in this case.
		cellElement.clickViaJavaScript();
	}
}

/**
 * Select or unselect all rows. This works only for tables where the first column is a selection box.
 *
 * @param selectIt Indicates whether the select-all box should be selected or unselected.
 */
public void selectAll(final boolean selectIt) {
	WebBrowserElement cellElement = getHeaderElements().get(0 /*index*/).findElement(By.xpath(".//input"));

	if (cellElement.isSelected() != selectIt) {
		// The regular click (WebBrowserElement.click()) may not work when browser window is zoomed out.
		// Clicking via JavaScript (WebBrowserElement.clickViaJavaScript()) may also not work when
		// browser window is zoomed out. Therefore, use a workaround as pressing the space bar to
		// perform the selection.
		cellElement.sendKeys(Keys.SPACE);
	}
}

/**
 * Wait for the table to refresh.
 * <p>
 * This method is called typically after a sort operation.
 * </p>
 */
abstract protected void waitForTableToRefresh();
}

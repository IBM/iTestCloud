package itest.cloud.ibm.page.element.ca.contentnav;

import static itest.cloud.ibm.page.element.ca.contentnav.CaTeamContentTabElement.AssetType.FOLDER;
import static itest.cloud.ibm.page.element.ca.contentnav.CaTeamContentTabElement.AssetType.REPORT;

import java.util.regex.Pattern;

import org.openqa.selenium.By;

import itest.cloud.ibm.page.ca.contentnav.CaContentPage;
import itest.cloud.ibm.page.ca.reporting.CaReportPage;
import itest.cloud.ibm.page.element.IbmElementWrapper;
import itest.cloud.ibm.page.element.IbmTabElement;
import itest.cloud.page.Page;
import itest.cloud.page.element.BrowserElement;
import itest.cloud.page.element.ElementWrapper;
import itest.cloud.scenario.error.InvalidArgumentError;
import itest.cloud.scenario.error.WaitElementTimeoutError;

public class CaTeamContentTabElement extends IbmTabElement {

public enum AssetType {
	FOLDER("Folder"),
	REPORT("Report");

	String label;

	AssetType(final String label) {
		this.label = label;
	}
}

public abstract class CaContentAssetElement extends IbmElementWrapper {

	public CaContentAssetElement(final ElementWrapper parent, final BrowserElement element, final String... data) {
		super(parent, element, data);
	}

	public String getName() {
		return this.data[0];
	}

	protected BrowserElement getOpenElement() {
		return waitForElement(By.xpath(".//a"));
	}

	@Override
	protected Pattern getExpectedTitle() {
		return null;
	}

	@Override
	protected By getTitleElementLocator() {
		return null;
	}
}

public class CaContentFolderElement extends CaContentAssetElement {

	public CaContentFolderElement(final ElementWrapper parent, final BrowserElement element, final String... data) {
		super(parent, element, data);
	}

	public void open() {
		getOpenElement().click();
	}

}

public class CaContentReportElement extends CaContentAssetElement {

	public CaContentReportElement(final ElementWrapper parent, final BrowserElement element, final String... data) {
		super(parent, element, data);
	}

	public CaReportPage open() {
		return openPageUsingLink(getOpenElement(), CaReportPage.class, getName());
	}
}


public CaTeamContentTabElement(final Page page, final String... data) {
	super(page, By.id(data[0]));
}

@Override
protected Pattern getExpectedTitle() {
	return null;
}

@Override
protected By getTitleElementLocator() {
	return null;
}

private CaContentAssetElement getImmediateAssetElement(final String name, final AssetType type) {
	final BrowserElement assetWebElement = waitForElement(By.xpath(
		".//*[@class = 'ba-tileView__asset']//label[.//*[text()='" + name + "'] and .//*[@title='" + type.label + "']]"));
	if(type == FOLDER) {
		return new CaContentFolderElement(this, assetWebElement, name);
	}
	else if(type == REPORT) {
		return new CaContentReportElement(this, assetWebElement, name);
	}
	throw new InvalidArgumentError("Asset type '" + type + "' is unknown to this method");
}

@Override
protected CaContentPage getPage() {
	return (CaContentPage) super.getPage();
}

//private static final String REPORT_PATH = "Samples/By business function/Customer experience/Reports/Daily agent activity";
// segments
private CaContentAssetElement getAssetElement(final String path, final AssetType type) {
	final String[] pathSegments = path.split("/");

	for (int i = 0; i < pathSegments.length - 1; i++) {
		final CaContentFolderElement folderElement = (CaContentFolderElement) getImmediateAssetElement(pathSegments[i], FOLDER);
		folderElement.open();

		// The header title of the Content Page should now be named after the folder as well.
		final String headerTitle = getPage().getHeaderTitle();
		if(!headerTitle.equals(pathSegments[i])) {
			throw new WaitElementTimeoutError("The header title of the Content Page was expected to be named after the opened folder '" + pathSegments[i] + "', but it it was incorrectly named '" + headerTitle + "' instead.");
		}

		final long timeoutMillis = timeout() * 1000 + System.currentTimeMillis();
		while (!getPage().getHeaderTitle().equals(pathSegments[i])) {
			if (System.currentTimeMillis() > timeoutMillis) {
				throw new WaitElementTimeoutError("The header title of the Content Page was expected to be named after the opened folder '" + pathSegments[i] + "', but it it was incorrectly named '" + headerTitle + "' instead (timeout = '" + timeout() + "s').");
			}
		}
	}
	return getImmediateAssetElement(pathSegments[pathSegments.length - 1], type);
}

public CaReportPage openReport(final String path) {
	final CaContentReportElement reportElement = (CaContentReportElement) getAssetElement(path, REPORT);
	return reportElement.open();
}

}
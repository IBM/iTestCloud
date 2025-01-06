package itest.cloud.scenario;

import java.net.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;

public class Step99_BruteForceExampleTests {

public void Android() throws MalformedURLException, InterruptedException {
	DesiredCapabilities capabilities =  new DesiredCapabilities();

	capabilities.setCapability("appium:deviceName","emulator-5554");
	capabilities.setCapability("platformName","Android");
	capabilities.setCapability("appium:automationName","uiautomator2");
	capabilities.setCapability("appium:app","/Users/swijenay/Downloads/app-release_35.apk");
	capabilities.setCapability("appium:platformversion", "14");
	capabilities.setCapability("appium:autoGrantPermissions", true);
	capabilities.setCapability("appium:chromedriverExecutable","/Users/Shared/itestcloud/drivers/chromedriver-113");
	URL url = URI.create("http://127.0.0.1:4723/").toURL();

	AndroidDriver driver = new AndroidDriver(url, capabilities);
	System.out.println("Execution Started for Android");
	Thread.sleep(2000);

	WebElement skipIntroElement = driver.findElement(AppiumBy.accessibilityId("ca-text-undefined"));
	skipIntroElement.click();
	Thread.sleep(2000);

	WebElement textElement = driver.findElement(By.xpath("//android.widget.TextView[@text=\"Let's connect to your server.\"]"));
	System.out.println(textElement.getText());

	WebElement enterServerURL = driver.findElement(By.xpath("//android.widget.TextView[@text=\"Manually enter your Server URL\"]"));
	enterServerURL.click();
	Thread.sleep(2000);

	driver.quit();
	System.out.println("Execution Completed for Android");
}

//private void switchToWindow(final Pattern urlPattern, final AndroidDriver driver) {
//	final Set<String> windowHandles = driver.getWindowHandles();
//
//	for (String windowHandle : windowHandles) {
//		driver.switchTo().window(windowHandle);
//
//		if(urlPattern.matcher(driver.getCurrentUrl()).matches()) return;
//	}
//
//	throw new ScenarioFailedError("A window with URL matching pattern '" + urlPattern + "' could not be found");
//}
//
//	private void show(final AndroidDriver driver, final WebElement element, final StringBuffer buffer) {
//		String tag = element.getTagName();
//		System.out.println(buffer.toString() + element.getTagName());
//		List<WebElement> elements = element.findElements(By.xpath("./*"));
//
//		if(!elements.isEmpty()) {
//			for (WebElement webElement : elements) {
//				if(!tag.equalsIgnoreCase("svg")) show(driver, webElement, new StringBuffer(buffer.toString() + " "));
//			}
//		}
//	}

public void IOS() throws MalformedURLException, InterruptedException {
    DesiredCapabilities capabilities =  new DesiredCapabilities();

    capabilities.setCapability("appium:deviceName","iPhone 16");
    capabilities.setCapability("platformName","iOS");
    capabilities.setCapability("appium:automationName","XCUITest");
    capabilities.setCapability("appium:bundleId", "com.ibm.ba.camobile");
	capabilities.setCapability("appium:app","/Users/swijenay/Downloads/CAMobile.app");
    capabilities.setCapability("appium:autoGrantPermissions", true);

    URL url = URI.create("http://127.0.0.1:4723/").toURL();

    IOSDriver driver = new IOSDriver(url, capabilities);
    System.out.println("Execution Started for iOS");
    Thread.sleep(2000);

    WebElement skipIntroElement = driver.findElement(AppiumBy.accessibilityId("ca-text-undefined"));
    skipIntroElement.click();
    Thread.sleep(2000);

    WebElement textElement = driver.findElement(By.xpath("//XCUIElementTypeStaticText[@label=\"Let's connect to your server.\"]"));
    System.out.println(textElement.getText());

    WebElement enterServerURL = driver.findElement(By.xpath("//XCUIElementTypeStaticText[@label=\"Manually enter your Server URL\"]"));
    enterServerURL.click();
    Thread.sleep(2000);

	driver.quit();
    System.out.println("Execution Completed for iOS");
}

public static void main(final String[] args) throws Exception {
	Step99_BruteForceExampleTests tests = new Step99_BruteForceExampleTests();
	tests.Android();
	tests.IOS();
}
}

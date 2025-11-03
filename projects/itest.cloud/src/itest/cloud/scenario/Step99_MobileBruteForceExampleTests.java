package itest.cloud.scenario;

import static io.appium.java_client.service.local.AppiumDriverLocalService.buildService;
import static io.appium.java_client.service.local.flags.GeneralServerFlag.LOG_LEVEL;

import java.net.*;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.service.local.AppiumDriverLocalService;
import io.appium.java_client.service.local.AppiumServiceBuilder;

public class Step99_MobileBruteForceExampleTests {

public void Android() throws MalformedURLException, InterruptedException {
	final DesiredCapabilities capabilities =  new DesiredCapabilities();

	capabilities.setCapability("appium:deviceName","emulator-5554");
	capabilities.setCapability("platformName","Android");
	capabilities.setCapability("appium:automationName","uiautomator2");
	capabilities.setCapability("appium:app","/Users/sudarsha/Downloads/app-release_35.apk");
	capabilities.setCapability("appium:platformversion", "15");
	capabilities.setCapability("appium:autoGrantPermissions", true);
	capabilities.setCapability("appium:chromedriverExecutable","/Users/Shared/itestcloud/drivers/chromedriver-113");

	final URL url = URI.create("http://127.0.0.1:4723/").toURL();

	final AndroidDriver driver = new AndroidDriver(url, capabilities);
	System.out.println("Execution Started for Android");
	Thread.sleep(2000);

	final WebElement skipIntroElement = driver.findElement(AppiumBy.accessibilityId("ca-text-undefined"));
	skipIntroElement.click();
	Thread.sleep(2000);

	final WebElement textElement = driver.findElement(By.xpath("//android.widget.TextView[@text=\"Let's connect to your server.\"]"));
	System.out.println(textElement.getText());

	final WebElement enterServerURL = driver.findElement(By.xpath("//android.widget.TextView[@text=\"Manually enter your Server URL\"]"));
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
	final DesiredCapabilities capabilities =  new DesiredCapabilities();

    capabilities.setCapability("appium:deviceName","iPhone 16");
    capabilities.setCapability("platformName","iOS");
    capabilities.setCapability("appium:platformVersion", "18.2");
    capabilities.setCapability("appium:automationName","XCUITest");
    capabilities.setCapability("appium:bundleId", "com.ibm.ba.camobile");
	capabilities.setCapability("appium:app","/Users/sudarsha/Downloads/CAMobile.app");
    capabilities.setCapability("appium:autoGrantPermissions", true);

	final AppiumDriverLocalService service = buildService(new AppiumServiceBuilder().withArgument(LOG_LEVEL, "warn").usingAnyFreePort());
	service.start();

    final IOSDriver driver = new IOSDriver(service, capabilities);
    System.out.println("Execution Started for iOS");
    Thread.sleep(2000);

    final WebElement skipIntroElement = driver.findElement(AppiumBy.accessibilityId("ca-text-undefined"));
    skipIntroElement.click();
    Thread.sleep(2000);

    final WebElement textElement = driver.findElement(By.xpath("//XCUIElementTypeStaticText[@label=\"Let's connect to your server.\"]"));
    System.out.println(textElement.getText());

    final WebElement enterServerURL = driver.findElement(By.xpath("//XCUIElementTypeStaticText[@label=\"Manually enter your Server URL\"]"));
    enterServerURL.click();
    Thread.sleep(2000);

	driver.quit();
	service.stop();
	service.close();
    System.out.println("Execution Completed for iOS");
}

public static void main(final String[] args) throws Exception {
	final Step99_MobileBruteForceExampleTests tests = new Step99_MobileBruteForceExampleTests();
	tests.Android();
	tests.IOS();
}
}

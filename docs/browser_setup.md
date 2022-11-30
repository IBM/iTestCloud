# Browser Setup
The framework is supposed to support Firefox, Google Chrome, Internet Explorer and Safari. However, only deep testing have been made using the Firefox and Chrome browsers.

Here are the setup recommendation for all these browsers.

### Google Chrome

Please complete the following steps to configure Google Chrome for developing and/or executing (running) tests:

1. Check the version of Chrome running on your system.
2. Download the Chrome driver matching the version of Chrome from [here](https://chromedriver.chromium.org/downloads).
3. Extract the downloaded Chrome Driver to a directory (e.g. /Users/Shared/itestcloud/drivers/)
4. (For macOS) Control-click the chromedriver executable and choose `Open` from the shortcut menu. This will take care of the problem of "Apple can’t check app for malicious software."
5. (Optional) To confirm the runtime environment of chromedriver is usable, with chromedriver running in a terminal, run the following command in a separate terminal session. Make sure no error is reported back from the curl command.
```
curl --header "Content-Type: application/json" --request POST --data '{"desiredCapabilities":{"browserName":"chrome"}}' http://localhost:9515/session
```
6. Create the profiles directory which will contain Chrome runtime files (e.g. /Users/Shared/itestcloud/profiles/chrome)
7. Clone the [vault repository](https://github.ibm.com/iTestCloud/com.ibm.itest.cloud.apsportal.vault) and open the `params/browser/chrome.properties` file, check the values of the following properties and update them as needed: `browserDriver`, and `browserProfile`. If you have used the above mentioned paths for the profile and Chrome driver, then no changes will be needed in `params/browser/chrome.properties`.

### Firefox

Please complete the following steps to configure FireFox 97.0.1 for developing and/or executing (running) tests:

1. Download [FireFox 97.0.1](https://ftp.mozilla.org/pub/firefox/releases/97.0.1/)
2. For Mac: If you use the .dmg file it will place the .app in the applications folder. Move the executables (exe/dmg) to a different path than the default (e.g. `/Users/Shared/itestcloud/browsers/Firefox_97.app/Contents/MacOS/firefox` to match with the firefox.properties used by the launch configurations). Note: if you match your FireFox installation with this path, you should not need to tweak the launch configurations or properties file when you run or debug a scenario from your workspace.
3. Download the [Gecko Driver](https://github.com/mozilla/geckodriver/releases/tag/v0.28.0) 
4. Extract the downloaded Gecko Driver to a directory (e.g. /Users/Shared/itestcloud/drivers/)
16. Clone `com.ibm.itest.cloud.apsportal.vault` repository and open the `params/browser/firefox.properties` file, check the values of the following properties and update them as needed: `browserPath`, `browserDriver`, and `browserProfile`. If you have used the above mentioned paths for the Firefox borwser, profile and Gecko driver, then no changes will be needed in `params/browser/firefox.properties`.

### Internet Explorer

To have Internet Explorer working properly while running your scenario, following setup has to be done: First, you'll need to download the Internet Explorer Driver from the Selenium website.

1. Get the driver from [here](http://selenium-release.storage.googleapis.com/index.html
) and put the executable accessible in the system path.
2. If you see an error message in the console along the lines of " Unexpected error launching Internet Explorer. Protected Mode must be set to the same value (enabled or disabled) for all zones,"
3. Open Internet Explorer
4. Open Internet Options
5. Click on the Security Tab
6. For each of the zones (Internet, Local intranet, Trusted sites, Restricted sites), check the box "Enable Protected Mode"

### Microsoft Edge
-To be provided. Please contact Sudarsha Wijenayake for details in the meantime.

### Safari
-To be provided. Please contact Sudarsha Wijenayake for details in the meantime.

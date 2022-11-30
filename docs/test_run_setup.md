# Using a personal account for test execution

To avoid conflicts on the cloud if tests are run by multiple users at the same time, you may want to conduct tests on a personal account.

1. Create an account on https://dataplatform.test.cloud.ibm.com/ using your IBMid
2. Edit the secret.properties file to include your own account details
	- For **tester, tester2, authenticator, testUser:**
	- `Username`: Your name
	- `UserID`: Your IBM email
	- `Email`: Your IBM email
	- `Password`: Your encrypted password (see below)
3. Add `-DobjectStorageName=CloudObjectStorage` to the scenario VM arguments in Eclipse --- the tests normally expect a different type of cloud storage than the one(s) initially available to a new user, and without this overridden argument, the automation will be unable to create a new project.

## Set up testing on a cluster
Edit the icp4d.properties file with the corresponding cluster URL and credentials.

## Adjust Eclipse VM arguments
Adjust the other VM arguments depending on what/how you want to test:
	- `DfirstStep`: The step/test group you want to start from. E.g. G02 (capitalization matters)
	- `DfirstTest`: The specific test within a step/test group you want to start from. E.g. 07
	- `DrunDeepDiveTests`: Usually reserved for Jenkins, when we want to comprehensively run the tests.
	- `DstopOnFailure`If you want to end the test executions on the first encountered failure.
	- `DverifyDependencies`: If you want to check that a test case's dependent tests are met before running it. **Important:** When skipping test cases through -DfirstStep, tests' dependent tests will not be met (because of skipping), so this verification should be set to false in many skipping situations.

## Encrypt your password
Find a way to run this code:

    System.out.println("%%%%%%%%%%%%%%%%%%");  
    System.out.println(com.ibm.itest.cloud.common.tests.utils.EncryptionUtils._encrypt_("YourPass"));  
    System.out.println("%%%%%%%%%%%%%%%%%%");

**One way:**

Go to the constructor of a *Data.java class and paste it right under the super() call, then run the scenario of the corresponding *Data.java class.

## Useful keyboard shortcuts
### Eclipse
**Searching for stuff**
`Command` + `Shift` + `R`: Search for **files** in the workspace by typing part of their names.
`Command` + `Shift` + `T`: Search for **types** in the workspace by typing part of their names.
`Command` + `Option` + `G`: Search for **highlighted string** within all files of a workspace.

**Managing your UI**
`Command` + `W`: Close currently opened file
`Command` + `Shift` + `W`: Close all opened files

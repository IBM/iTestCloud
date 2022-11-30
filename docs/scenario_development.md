#Test Scenario Development

Once Eclipse and browser have been configured, you're ready to develop a test scenario as described below. 

1. Contact an administrator of the iTestCloud project and get a repository created for your new test scenario in the [iTestCloud organization](https://github.ibm.com/iTestCloud)
1. Clone the following GitHub repositories on to your computer: [Common Layer](https://github.ibm.com/iTestCloud/com.ibm.itest.cloud.common), [APS Portal Layer](https://github.ibm.com/iTestCloud/com.ibm.itest.cloud.apsportal), [APS Portal Demo Scenario](https://github.ibm.com/iTestCloud/com.ibm.itest.cloud.apsportal.demo)
1. Create a new issue in the [Planning Repository](https://github.ibm.com/iTestCloud/Planning) to track the development of your test scenario
1. Create a new branch in your test scenario repository with a name in the following format: \<your GitHub id\>_\<newly created issue id\>. For example, **swijenay_39**
1. Clone the repository created for your new test scenario on to your computer
1. Switch to the newly created branch in your test scenario repository
1. Import all above mentioned repositories (4 in total) into Eclipse as existing projects
1. Ensure that the new branch has been selected for your test scenario repository/project in Eclipse
1. Copy the contents of the demo test scenario project (com.ibm.itest.cloud.apsportal.demo) into your test scenario project
1. Refactor the copied contents (such as Java packages, classes, comments, ...etc.) in your new test scenario project appropriately
1. Commit your changes to the new branch
1. Develop new test-cases in the new test scenario project
1. Once your test cases have been developed, commit your changes to the new branch, create a pull request and ask an administrator of the iTestCloud project to review your changes.
1. The administrator will merge your changes to the master branch of your test scenario repository upon the completion of the review.
 

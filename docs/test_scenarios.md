# DSX/DH Portal UI Test Automation Coverage
The following test scenarios have been developed with the iTestCloud Framework by a number of teams and individuals.

1. [DSX and DH Regression Scenario](https://github.ibm.com/iTestCloud/com.ibm.itest.cloud.apsportal.regression)
2. [Documentation Scenario](https://github.ibm.com/iTestCloud/com.ibm.itest.cloud.apsportal.documentation)
3. [Streaming Scenario](https://github.ibm.com/iTestCloud/com.ibm.itest.cloud.apsportal.streams)
4. [Exchange Scenario](https://github.ibm.com/iTestCloud/com.ibm.itest.cloud.apsportal.exchange)
5. [Catalog Scenario](https://github.ibm.com/iTestCloud/com.ibm.itest.cloud.apsportal.catalog)

## Test Coverage
The following are the areas, features and/or use cases validated by the above mentioned test scenarios. 

## Generic
1. Validation of the home pages of the Data Hub and Data Science Experience
1. User profile management and customization
1. Object Storage creation, management, utilization and deletion
1. Data-connection creation, management, sharing, utilization and deletion
1. Creation, management, utilization and deletion of projects with Object Storage or catalogs
1. Data connection inclusion, management and deletion within a project
1. Collaborators inclusion, permission management and deletion within a project
1. Validation for existence and deletion of bookmarks of community data sets, notebooks, articles and tutorials within a project
1. Validation for existence and deletion of bookmarks of community data sets, notebooks, articles and tutorials within the Explore Community sidebar (menu)

## Data Import
1. Data services creation, management, utilization and deletion
1. Data import from a CSV file on the local file system into a cloud data service
1. Data import from a CSV file in the Object Storage into a cloud data service
1. Data import from a cloud data service (Cloudant) into another cloud data service (dashDB) via a private data connection
1. Data import from a cloud data service (Cloudant) into another cloud data service (dashDB) via a shared data connection by an alternate user

### Catalogs
1. Catalog creation, management, utilization and deletion 
1. Data set with or without tags creation, management, utilization and deletion within a catalog
1. Data set format validation and alteration within a catalog
1. Visibility validation and alteration within a catalog
1. Collaborators inclusion, permission management and deletion within a catalog
1. Owner and administrators inclusion, management and deletion within a catalog
1. Validation of the following charts in the Dashboards tab of populated and empty catalogs: Discovered Business Types, Business Type Fields, Catalog Statistics, Assets in Use, Top 10 Users, and Top 10 Assets.
1. Inclusion of data sets in a catalog to a project
1. Validation of the history of a catalog

### Exchange
1. Validation of following properties and features of the first 25 data sets, notebooks, articles and tutorials via their community cards in the Exchange: author, date, topic, level, format, like feature, and bookmarking feature
1. Validation of following properties and features of the first 25 data sets, notebooks, articles and tutorials via their community pages in the Exchange: topic, last updated date, created date, publisher, preview, column details, like feature, shared link (permalink), and bookmarking feature

### Documentation
1. Validation of the following tabs: All, Get Started, Analyze Data, Manage Data, and Integrated Tools
1. Validation of the tables of contents in the above mentioned tabs
1. Opening of all articles in all tabs via the corresponding table of contents
1. Opening various articles via their sharable URLs (permalinks)
1. Locating and opening various articles by utilizing the search feature
1. Locating and opening various articles by utilizing the Explore Community sidebar (menu)
1. Opening various articles via their sharable URLs (permalinks)
1. Validation of the like, book-marking, bread-crumb and go-to-top features

### Streaming
1. Message Hub service creation, management, utilization and deletion
2. Validation of the Ingest Streaming Data tab
3. Topic creation, management, and deletion within a Message Hub service instance

### RStudio
1. Validation for existence of various features such as disk usage bar, menu, and console

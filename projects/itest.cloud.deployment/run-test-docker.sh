#!/bin/bash

function errorExit () {
	echo "$1" 1>&2
	exit 1
}

echo -e ">>>>>>>>>>>>>> Prerequisites"
sudo apt-get update
sudo apt-get install -y software-properties-common

echo -e ">>>>>>>>>>>>>> Installing all locales"
sudo add-apt-repository main || errorExit "Enabling main repository failed, exiting"
sudo add-apt-repository universe || errorExit "Enabling universe repository failed, exiting"
sudo add-apt-repository multiverse || errorExit "Enabling multiverse repository failed, exiting"
sudo add-apt-repository restricted || errorExit "Enabling restricted repository failed, exiting"

sudo apt-get install locales-all || errorExit "Installing all locales failed, exiting"

export LANG=en_US.UTF-8 || errorExit "Setting LANG variable failed, exiting"

if [ ${BROWSER} = "FireFox" ]
then
	echo -e ">>>>>>>>>>>>>> Prerequisites for Firefox"
	sudo apt-get install -y libxtst6 libgtk-3-0 libx11-xcb-dev libdbus-glib-1-2 libxt6 libpci-dev || errorExit "Installing Firefox prerequisites failed, exiting"

	echo -e ">>>>>>>>>>>>>> Downloading Firefox"
	cd /opt || errorExit "Changing dir to /opt failed, exiting"
	wget https://ftp.mozilla.org/pub/firefox/releases/126.0.1/linux-x86_64/en-US/firefox-126.0.1.tar.bz2 || errorExit "Downloading Firefox failed, exiting"
	
	echo -e ">>>>>>>>>>>>>> Extracting Firefox"
	tar -xjvf ./firefox-126.0.1.tar.bz2 || errorExit "Extracting Firefox failed, exiting"
	
	echo -e ">>>>>>>>>>>>>> Deleting Firefox installer"
	rm -f ./firefox-126.0.1.tar.bz2 || errorExit "Deleting Firefox installer failed, exiting"

	echo -e ">>>>>>>>>>>>>> Downloading Gecko driver archive"
	wget https://github.com/mozilla/geckodriver/releases/download/v0.34.0/geckodriver-v0.34.0-linux64.tar.gz || errorExit "Downloading Gecko driver archive failed, exiting"
	
	echo -e ">>>>>>>>>>>>>> Extracting Gecko driver archive"
	tar -xzvf ./geckodriver-v0.34.0-linux64.tar.gz || errorExit "Extracting Gecko driver archive failed, exiting"
	chmod +x ./geckodriver || errorExit "Setting execution permission for Gecko driver failed, exiting"
	
	echo -e ">>>>>>>>>>>>>> Deleting Gecko driver archive"
	rm -f ./geckodriver-v0.34.0-linux64.tar.gz || errorExit "Deleting Gecko driver archive failed, exiting"

	export FF_PATH=firefox/firefox
	export FF_DRIVER=geckodriver
fi

if [ ${BROWSER} = "Chrome" ]
then
	echo -e ">>>>>>>>>>>>>> Downloading Chrome"
	wget -nv https://dl.google.com/linux/direct/google-chrome-stable_current_amd64.deb || errorExit "Downloading Chrome failed, exiting"

	echo -e ">>>>>>>>>>>>>> Installing Chrome"
	sudo apt install --fix-broken -y ./google-chrome-stable_current_amd64.deb || errorExit "Installing Chrome failed, exiting"

	echo -e ">>>>>>>>>>>>>> Downloading Chrome driver archive"
	wget https://storage.googleapis.com/chrome-for-testing-public/127.0.6533.72/linux64/chromedriver-linux64.zip || errorExit "Downloading Chrome driver archive failed, exiting"

	echo -e ">>>>>>>>>>>>>> Extracting Chrome driver archive"
	unzip ./chromedriver-linux64.zip || errorExit "Extracting Chrome driver archive failed, exiting"
	chmod +x ./chromedriver-linux64/chromedriver || errorExit "Setting execution permission for Chrome driver failed, exiting"

	echo -e ">>>>>>>>>>>>>> Deleting Chrome driver archive"
	rm -f ./chromedriver-linux64.zip || errorExit "Deleting Chrome driver archive failed, exiting"

	export CHROME_DRIVER=chromedriver-linux64/chromedriver
fi

# if [ ${BROWSER} == "Random" ]
# then
# 	D=$(date +%d)
# 	R=$(expr ${D} % 2)
# 	if [ ${R} -eq 0 ]
# 	then
# 		export BROWSER="FireFox"
# 	else
# 		export BROWSER="Chrome"
# 	fi
# 	echo -e ">>>>>>>>>>>>>> ${BROWSER} was selected as browser to run tests"
# fi

export CLASSPATH=projects/com.ibm.itest.cloud.common/libs/junit-4.12.jar:projects/com.ibm.itest.cloud.common/libs/hamcrest-core-1.3.jar || errorExit "Defining class path failed, exiting"

echo -e ">>>>>>>>>>>>>> Starting xvfb"
Xvfb -ac :99 -screen 0 1280x1024x16 -nolisten unix &
export DISPLAY=:99

cp projects/itest.cloud.deployment/build.xml build.xml || errorExit "Copying build.xml failed, exiting"

ant run -Dtest=${1} -Dlocale="${LOCALE}" -Denvironment="${ENVIRONMENT}" -Dprefix="${PREFIX}" -Dparams="${PARAMS}" -Dheadless=true -Dperformance="${PERFORMANCE}" || errorExit "Invoking test scenario failed, exiting"

if [[ -z ${ARTIFACTORY_ACCOUNT} ]];
then
	echo "ARTIFACTORY_ACCOUNT not set; skipping upload..."
else
	LOG_ZIP=${BUILD_NUMBER}.zip || errorExit "Defining name of test results archive failed, exiting"
	zip -r ${LOG_ZIP} build/report.xml build/debug/ || errorExit "Zipping test results failed, exiting"
	curl -u ${ARTIFACTORY_ACCOUNT} -X PUT https://na.artifactory.swg-devops.com/artifactory/wcp-itestcloud-results-generic-local/${2}/${LOG_ZIP} -T ${LOG_ZIP}
fi
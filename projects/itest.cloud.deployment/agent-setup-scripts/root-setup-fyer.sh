#######################################################################
# Copyright (c) 2022 IBM Corporation and others.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#######################################################################

#!/bin/bash

function errorExit () {
	echo "$1" 1>&2
	exit 1
}

echo -e ">>>>>>>>>>>>>> Updating installed packages"
yum -y update || errorExit "Updating installed packages failed, exiting"

echo -e ">>>>>>>>>>>>>> Installing GNOME Desktop Manager"
yum -y groupinstall 'Server with GUI' || errorExit "Installing GNOME Desktop Manager failed, exiting"

echo -e ">>>>>>>>>>>>>> Setting GNOME Desktop Manager to be default desktop environment"
systemctl set-default graphical.target || errorExit "Setting GNOME Desktop Manager to be default desktop environment failed, exiting"

echo -e ">>>>>>>>>>>>>> Installing VNC"
yum -y install tigervnc-server || errorExit "Installing VNC failed, exiting"

echo -e ">>>>>>>>>>>>>> Enabling rhel-7-server-optional-rpms repository"
subscription-manager repos --enable=rhel-7-server-optional-rpms || errorExit "Enabling rhel-7-server-optional-rpms repository failed, exiting"

echo -e ">>>>>>>>>>>>>> Installing Java SDK 1.8"
yum -y install java-1.8.0-openjdk-devel || errorExit "Installing Java SDK 1.8 failed, exiting"

echo -e ">>>>>>>>>>>>>> Enabling rhel-server-rhscl-7-rpms repository"
subscription-manager repos --enable=rhel-server-rhscl-7-rpms || errorExit "Enabling rhel-server-rhscl-7-rpms repository failed, exiting"

echo -e ">>>>>>>>>>>>>> Installing Ant-Junit package"
yum -y install rh-java-common-ant-junit || errorExit "Installing Ant-Junit package failed, exiting"

echo -e ">>>>>>>>>>>>>> Copying Ant to /usr/bin"
/bin/cp -rf /opt/rh/rh-java-common/root/usr/bin/ant* /usr/bin/ || errorExit "Copying Ant to /usr/bin, exiting"

echo -e ">>>>>>>>>>>>>> Installing Zip package"
yum -y install zip || errorExit "Installing Zip package failed, exiting"

echo -e ">>>>>>>>>>>>>> Installing Unzip package"
yum -y install unzip || errorExit "Installing Unzip package failed, exiting"

echo -e ">>>>>>>>>>>>>> Installing Git package"
yum -y install git || errorExit "Installing Git package failed, exiting"

echo -e ">>>>>>>>>>>>>> Installing development tools package"
yum -y install gcc-c++ make || errorExit "Installing development tools package failed, exiting"

#echo -e ">>>>>>>>>>>>>> Enabling Node YUM repository"
#curl -sL https://rpm.nodesource.com/setup_10.x | bash - || errorExit "Enabling Node YUM repository failed, exiting"

#echo -e ">>>>>>>>>>>>>> Installing Node package"
#yum -y install nodejs || errorExit "Installing Node package failed, exiting"

echo -e ">>>>>>>>>>>>>> Enabling Google YUM repository"
echo -e "[google-chrome]\nname=google-chrome\nbaseurl=http://dl.google.com/linux/chrome/rpm/stable/\$basearch\nenabled=1\ngpgcheck=1\ngpgkey=https://dl-ssl.google.com/linux/linux_signing_key.pub" > /etc/yum.repos.d/google-chrome.repo || errorExit "Enabling Google YUM repository failed, exiting"

echo -e ">>>>>>>>>>>>>> Installing Google Chrome Stable package"
yum -y install google-chrome-stable --nogpgcheck || errorExit "Installing Google Chrome Stable package failed, exiting"

echo -e ">>>>>>>>>>>>>> Downloading Firefox"
cd /opt || errorExit "Changing dir to /opt failed, exiting"
wget https://ftp.mozilla.org/pub/firefox/releases/124.0.1/linux-x86_64/en-US/firefox-124.0.1.tar.bz2 || errorExit "Downloading Firefox failed, exiting"

echo -e ">>>>>>>>>>>>>> Extracting Firefox"
tar -xjvf firefox-124.0.1.tar.bz2 || errorExit "Extracting Firefox failed, exiting"

echo -e ">>>>>>>>>>>>>> Deleting Firefox installer"
rm -f firefox-124.0.1.tar.bz2 || errorExit "Deleting Firefox installer failed, exiting"

echo -e ">>>>>>>>>>>>>> Setting write permission for /run/user/0 which is required for FireFox"
chmod -R 777 /run/user/0

echo -e ">>>>>>>>>>>>>> Removing directory /opt/drivers"
rm -rf /opt/drivers/ || errorExit "Removing directory /opt/drivers failed, exiting"

echo -e ">>>>>>>>>>>>>> Making directory /opt/drivers"
mkdir /opt/drivers || errorExit "Making directory /opt/drivers failed, exiting"

echo -e ">>>>>>>>>>>>>> Changing directory to /opt/drivers"
cd /opt/drivers/ || errorExit "Changing directory to /opt/drivers failed, exiting"

echo -e ">>>>>>>>>>>>>> Downloading Chrome driver archive"
wget https://storage.googleapis.com/chrome-for-testing-public/123.0.6312.86/linux64/chromedriver-linux64.zip || errorExit "Downloading Chrome driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Extracting Chrome driver archive"
unzip chromedriver-linux64.zip || errorExit "Extracting Chrome driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Deleting Chrome driver archive"
rm -f chromedriver-linux64.zip || errorExit "Deleting Chrome driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Downloading Gecko driver archive"
wget https://github.com/mozilla/geckodriver/releases/download/v0.34.0/geckodriver-v0.34.0-linux64.tar.gz || errorExit "Downloading Gecko driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Extracting Gecko driver archive"
tar -xzvf geckodriver-v0.34.0-linux64.tar.gz || errorExit "Extracting Gecko driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Deleting Gecko driver archive"
rm -f geckodriver-v0.34.0-linux64.tar.gz || errorExit "Deleting Gecko driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Creating automation user"
useradd automation || errorExit "Creating automation user failed, exiting"

echo -e ">>>>>>>>>>>>>> Setting password of automation user"
passwd automation || errorExit "Setting password of automation user failed, exiting"

echo -e ">>>>>>>>>>>>>> Adding Jenkins commands to /etc/rc.local"
read -p "Enter VM name in Jenkins: " VMNAME
read -p "Enter Secret of VM in Jenkins: " SECRET
P1="\nsu automation -c \"java -jar /home/automation/jenkins/agent.jar -jnlpUrl http://sottdevperf1.fyre.ibm.com:8080/computer/"
P2="/jenkins-agent.jnlp -secret "
P3=" -workDir /home/automation/jenkins &\""
echo -e ${P1}${VMNAME}${P2}${SECRET}${P3} >> /etc/rc.local || errorExit "Adding Jenkins commands to /etc/rc/local failed, exiting"
chmod +x /etc/rc.local || errorExit "Setting execution permission for /etc/rc.local failed, exiting"

echo -e ">>>>>>>>>>>>>> Rebooting"
reboot

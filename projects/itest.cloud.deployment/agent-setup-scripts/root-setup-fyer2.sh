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

echo -e ">>>>>>>>>>>>>> Installing Java SDK 1.8"
yum -y install java-1.8.0-openjdk-devel || errorExit "Installing Java SDK 1.8 failed, exiting"

echo -e ">>>>>>>>>>>>>> Installing Ant-Junit package"
yum -y install ant-junit || errorExit "Installing Ant-Junit package failed, exiting"

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
yum -y install google-chrome-stable || errorExit "Installing Google Chrome Stable package failed, exiting"

echo -e ">>>>>>>>>>>>>> Downloading Firefox"
cd /opt || errorExit "Changing dir to /opt failed, exiting"
wget https://ftp.mozilla.org/pub/firefox/releases/97.0.1/linux-x86_64/en-US/firefox-97.0.1.tar.bz2 || errorExit "Downloading Firefox failed, exiting"

echo -e ">>>>>>>>>>>>>> Extracting Firefox"
tar -xjvf firefox-97.0.1.tar.bz2 || errorExit "Extracting Firefox failed, exiting"

echo -e ">>>>>>>>>>>>>> Deleting Firefox installer"
rm -f firefox-97.0.1.tar.bz2 || errorExit "Deleting Firefox installer failed, exiting"

echo -e ">>>>>>>>>>>>>> Setting write permission for /run/user/0 which is required for FireFox"
chmod -R 777 /run/user/0

echo -e ">>>>>>>>>>>>>> Removing directory /opt/drivers"
rm -rf /opt/drivers/ || errorExit "Removing directory /opt/drivers failed, exiting"

echo -e ">>>>>>>>>>>>>> Making directory /opt/drivers"
mkdir /opt/drivers || errorExit "Making directory /opt/drivers failed, exiting"

echo -e ">>>>>>>>>>>>>> Changing directory to /opt/drivers"
cd /opt/drivers/ || errorExit "Changing directory to /opt/drivers failed, exiting"

echo -e ">>>>>>>>>>>>>> Downloading Chrome driver archive"
wget https://chromedriver.storage.googleapis.com/95.0.4638.54/chromedriver_linux64.zip || errorExit "Downloading Chrome driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Extracting Chrome driver archive"
unzip chromedriver_linux64.zip || errorExit "Extracting Chrome driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Deleting Chrome driver archive"
rm -f chromedriver_linux64.zip || errorExit "Deleting Chrome driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Downloading Gecko driver archive"
wget https://github.com/mozilla/geckodriver/releases/download/v0.28.0/geckodriver-v0.28.0-linux64.tar.gz || errorExit "Downloading Gecko driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Extracting Gecko driver archive"
tar -xzvf geckodriver-v0.28.0-linux64.tar.gz || errorExit "Extracting Gecko driver archive failed, exiting"

echo -e ">>>>>>>>>>>>>> Deleting Gecko driver archive"
rm -f geckodriver-v0.28.0-linux64.tar.gz || errorExit "Deleting Gecko driver archive failed, exiting"

#download Anaconda3 installer
echo -e ">>>>>>>>>>>>>> Downloading Anaconda3 installer"
wget https://repo.anaconda.com/archive/Anaconda3-2020.11-Linux-x86_64.sh || errorExit "Failed to download Anaconda installation package."

#Install to default location /root/Anaconda3 with silent mode
echo -e ">>>>>>>>>>>>>> Installing to default location /root/Anaconda3 with silent mode"
bash ./Anaconda3-2020.11-Linux-x86_64.sh -b -f || errorExit "Installing Anaconda failed."

#Source and activate conda
echo -e ">>>>>>>>>>>>>> Sourcing and activating conda"
source /root/anaconda3/bin/activate || errorExit "Failed to source anaconda."
conda config --set auto_activate_base False || errorExit "Failed to activate conda."

# Create env
echo -e ">>>>>>>>>>>>>> Creating env"
conda create -y -n fl_env4 python=3.7.10 || errorExit "Failed to create env."

# Install jupyter in env
echo -e ">>>>>>>>>>>>>> Installing jupyter in env"
conda install -y -c conda-forge -n fl_env4 notebook || errorExit "Failed to install jupyter in ev."

# Activate env
echo -e ">>>>>>>>>>>>>> Activating env"
conda activate fl_env4 || errorExit "Failed to activate Federated Learning environment"

# Pip install SDK
echo -e ">>>>>>>>>>>>>> Installing SDK"
yes | pip install ibm-watson-machine-learning  || errorExit "Failed to install ibm watson machine learning SDK"

# Pip install packages needed by IBMFL
echo -e ">>>>>>>>>>>>>> Installing packages needed by IBMFL"
yes | pip install environs parse websockets==8.1 jsonpickle==1.4.1 pandas pytest pyYAML requests pathlib2 psutil setproctitle tabulate lz4 opencv-python gym ray==0.8.0 cloudpickle==1.3.0 image  || errorExit "Failed to install required libraries."

# Pip install frameworks used by IBMFL
echo -e ">>>>>>>>>>>>>> Installing frameworks used by IBMFL"
yes | pip install tensorflow==2.4.1 scikit-learn==0.23.2 keras==2.2.4 numpy==1.19.5 scipy==1.4.1 torch==1.7.1 skorch || errorExit "Failed to install required Frameworks used by IBMFL"

# Set execution permission for anaconda3
echo -e ">>>>>>>>>>>>>> Setting execution permission for anaconda3"
chmod 777 /root/anaconda3 || errorExit "Failed to set permission for Anaconda3"

echo -e ">>>>>>>>>>>>>> Deleting Anaconda3 installer"
rm -f /root/Anaconda3-2020.11-Linux-x86_64.sh || errorExit "Failed to delete Anaconda3 installer"

echo -e ">>>>>>>>>>>>>> Creating automation user"
useradd automation || errorExit "Creating automation user failed, exiting"

echo -e ">>>>>>>>>>>>>> Setting password of automation user"
passwd automation || errorExit "Setting password of automation user failed, exiting"

echo -e ">>>>>>>>>>>>>> Adding Jenkins commands to /etc/rc.local"
read -p "Enter VM name in Jenkins: " VMNAME
read -p "Enter Secret of VM in Jenkins: " SECRET
P1="\nsu automation -c \"java -jar /home/automation/jenkins/agent.jar -jnlpUrl https://wcp-itestcloud-jenkins.swg-devops.com/computer/"
P2="/slave-agent.jnlp -secret "
P3=" -workDir /home/automation/jenkins &\""
echo -e ${P1}${VMNAME}${P2}${SECRET}${P3} >> /etc/rc.local || errorExit "Adding Jenkins commands to /etc/rc/local failed, exiting"
chmod +x /etc/rc.local || errorExit "Setting execution permission for /etc/rc.local failed, exiting"

echo -e ">>>>>>>>>>>>>> Rebooting"
reboot

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

#!/bin/sh

hostFile="./jenkinsNodes.txt"

usage_push() {
  echo "Usage: $0 push <source> <target>"
  echo ""
  echo "specify source file or directory"
  echo "Example: $0 push /sourcedir1/file  /targetdir1/"
  echo "         $0 push /sourcedir1/dir2 /targetdir1/"
}

usage_execute() {
  echo "Usage: $0 execute <scriptToExecute> <arguments>"
  echo ""
  echo "Specify script to execute followed by arguments to pass to the scripts."
  echo "Example: $0 execute /tmp/installPythonClient.sh "fl_env35" "1.0.175.2""
}

#push the executions scripts from source directory to all the hosts
#destination directory.
push() {
  if [  $# -le 1 ]
    then
    usage_push
    exit 1
  fi

  sourcePath=$1
  targetPath=$2

  #Read the hosts list
  IFS=$'\n' read -d '' -r -a hosts_list < $hostFile
  
  for i in ${hosts_list[@]}
    do
       echo "pushing files $sourcePath to host $i:/$targetPath"
       scp -r -oStrictHostKeyChecking=no $sourcePath root@$i:$targetPath
    done
}

#execute the given scripts on the remote hosts.
execute(){
  if [  $# -le 0 ]
    then
    usage_execute
    exit 1
  fi


  scriptName=$1
  set -- "${@:2}"

  #Read the hosts list
  IFS=$'\n' read -d '' -r -a hosts_list < $hostFile

  for i in ${hosts_list[@]}
    do
      echo "Executing script $scriptName on host $i"
      ssh -oStrictHostKeyChecking=no root@$i "$scriptName $@"
    done

}

#Main
if [ "x$1" != "xpush" ] && [ "x$1" != "xexecute" ]; then
  echo "usage: ./deploy.sh push | execute [..]"
  echo "$0 push <source> <target>"
  echo "$0 execute <scriptToExecute> <arguments>"
  exit
fi

"$@"

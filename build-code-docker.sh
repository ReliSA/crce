#!/bin/bash
BUILD_SCRIPT_PATH="build-code.bash"
DEPLOY_PATH="deploy"
echo '
  ____ ____   ____ _____ 
 / ___|  _ \ / ___| ____|
| |   | |_) | |   |  _|  
| |___|  _ <| |___| |___ 
 \____|_| \_\\____|_____|
                         
'

echo "Docker building..."
docker image build --tag builder:1.0 ./build
echo "Docker building...[DONE]"
echo "MVN building..."
bash $BUILD_SCRIPT_PATH $1
echo "MVN building...[DONE]"
cd $DEPLOY_PATH
echo "Preparing bundles..."
mvn clean pax:directory
./prepare-bundles.sh
echo "Preparing bundles...[DONE]"
echo "Docker building..."
docker build . -t ${image-tag}
echo "Docker building...[DONE]"
echo "Starting CRCE application..."  
docker run -d -p 27017:27017 -v ~/data_:/data/db mongo
docker run -it -p 8080:8080 --network="host" --add-host mongoserver:127.0.0.1 -v /felix/deploy:/felix/deploy ${image-tag}

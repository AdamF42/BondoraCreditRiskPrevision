#!/bin/bash

function trap_handler()
{
        MYSELF="$0"
        LASTLINE="$1"
        LASTERR="$2"
        echo "${MYSELF}: line ${LASTLINE}: exit status of last command: ${LASTERR}"
}


SPARK_FOLDER_NAME="spark-2.4.4-bin-hadoop2.7.tgz"
SPARK_INSTALL_LOCATION="/opt"
# Specify the URL to download Spark from
SPARK_URL=http://archive.apache.org/dist/spark/spark-2.4.4/spark-2.4.4-bin-hadoop2.7.tgz

set -e

trap 'trap_handler ${LINENO} $?' ERR

curl $SPARK_URL > $SPARK_INSTALL_LOCATION/$SPARK_FOLDER_NAME

cd $SPARK_INSTALL_LOCATION

tar -zxvf $SPARK_FOLDER_NAME

SPARK_FOLDER_NAME=$(echo $SPARK_FOLDER_NAME | sed -e "s/.tgz$//")
echo "export SPARK_HOME=/opt/$SPARK_FOLDER_NAME" >> ~/.bashrc
echo "export PATH=\$SPARK_HOME/bin:\$PATH" >> ~/.bashrc

sudo add-apt-repository ppa:webupd8team/java && sudo apt-get -y update 

sudo apt-get -y install openjdk-8-jdk

sudo apt-get -y install automake autotools-dev fuse g++ git libcurl4-gnutls-dev libfuse-dev libssl-dev libxml2-dev make pkg-config

sudo apt-get -y install awscli

sudo apt-get -y install node-gyp

sudo apt-get -y install nodejs

sudo apt-get -y install npm

npm install express

source ~/.bashrc
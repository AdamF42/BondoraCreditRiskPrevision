#!/bin/bash

KEY=$1
BUCKET=$2
SUBNET=$3

NAME="SCPCluster"
REGION="us-east-1"
RELEASE="emr-5.29.0"
INSTANCE_TYPE="m5.xlarge"
NODES=6
ASSEMBLY="emr-assembly-0.1.jar"

function trap_handler()
{
        MYSELF="$0"
        LASTLINE="$1"
        LASTERR="$2"
        echo "${MYSELF}: line ${LASTLINE}: exit status of last command: ${LASTERR}"
}

if [ $# -lt "3" ]; then
  echo "Usage:  $0 <pem-keys> <bucket-name> <subnet>"
  exit 1;
fi

command -v "aws" >/dev/null 2>&1
if [[ $? -ne 0 ]]; then
    echo "I require aws but it's not installed. Abort."
    exit 1
fi

set -e

trap 'trap_handler ${LINENO} $?' ERR

aws emr create-cluster \
--release-label $RELEASE  \
--region $REGION \
--instance-groups InstanceGroupType=MASTER,InstanceCount=1,InstanceType=$INSTANCE_TYPE InstanceGroupType=CORE,InstanceCount=$[NODES - 1],InstanceType=$INSTANCE_TYPE \
--use-default-roles \
--ec2-attributes SubnetIds=subnet-$SUBNET,KeyName=$KEY \
--applications Name=Spark \
--name=$NAME \
--log-uri s3://"$BUCKET" \
--steps \
Type=CUSTOM_JAR,Name="Copy $ASSEMBLY to local",ActionOnFailure=TERMINATE_CLUSTER,Jar=s3://$REGION.elasticmapreduce/libs/script-runner/script-runner.jar,Args=["s3://$BUCKET/cp_assembly.sh, $BUCKET"] \
Type=Spark,Name="Spark submit $ASSEMBLY",ActionOnFailure=TERMINATE_CLUSTER,Args=["--deploy-mode, client, --class, Main, ../$ASSEMBLY,s3://$BUCKET"] \
--auto-terminate
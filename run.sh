#!/bin/bash

NAME="SCPCluster"
BUCKET=$1
REGION="us-east-1a"
RELEASE="emr-5.29.0"
SUBNET=""
KEY=""

if [ -n "$1" ]; then # If first parameter passed then print Hi

	BUCKET=$1

else
    echo "No parameter bucket found. "
    exit
fi


aws emr create-cluster 
--release-label $RELEASE  
--instance-groups InstanceGroupType=MASTER,InstanceCount=1,InstanceType=m4.xlarge InstanceGroupType=CORE,InstanceCount=1,InstanceType=m4.xlarge 
--use-default-roles 
--ec2-attributes SubnetIds=subnet-$SUBNET,KeyName=$KEY 
--applications Name=Spark Name=YARN Name=Zeppelin 
--name=$NAME
--log-uri s3://$BUCKET 
--steps Type=CUSTOM_JAR,Name=CustomJAR,ActionOnFailure=CONTINUE,Jar=s3://$REGION.elasticmapreduce/libs/script-runner/script-runner.jar,Args=["s3://$BUCKET/start.sh, $BUCKET"]
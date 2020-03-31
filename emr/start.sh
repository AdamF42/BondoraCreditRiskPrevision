#!/bin/bash

ASSEMBLY="emr-assembly-0.1.jar"

if [ -n "$1" ]; then # If first parameter passed then print Hi

	BUCKET=$1

else
    echo "No parameter bucket found. "
    exit
fi

aws s3 cp s3://scp-project/$ASSEMBLY .

spark-submit $ASSEMBLY s3://$BUCKET
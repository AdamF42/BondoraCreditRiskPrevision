#!/bin/bash

BUCKET=$1

cd "../"

command_exists() {
    # check if command exists and fail otherwise
    command -v "$1" >/dev/null 2>&1
    if [[ $? -ne 0 ]]; then
        echo "$1 is required but it's not installed. Abort."
        exit 1
    fi
}

function trap_handler()
{
        MYSELF="$0"
        LASTLINE="$1"
        LASTERR="$2"
        echo "${MYSELF}: line ${LASTLINE}: exit status of last command: ${LASTERR}"
}

if [ -z "$1" ]; then
  echo "Usage: build_and_deply.sh  <bucket-name>"
  exit 1;
fi

for COMMAND in "aws" "sbt"; do
    command_exists "${COMMAND}"
done

set -e

trap 'trap_handler ${LINENO} $?' ERR

echo "#################### BUILDING ASSEMBLY #####################"
sbt clean assembly

echo "#################### RUNNING TESTS #########################"
sbt test

echo "#################### COPYING EMR ASSEMBLY ##################"
aws s3 cp emr/target/scala-2.11/emr-assembly-0.1.jar s3://$BUCKET

echo "#################### COPYING EMR SCRIPT ####################"
aws s3 cp scripts/cp_assembly.sh s3://$BUCKET

echo "#################### COPYING EC2 ASSEMBLY  ##################"
aws s3 cp ec2/target/scala-2.11/ec2-assembly-0.1.jar s3://$BUCKET
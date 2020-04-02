#!/bin/bash

ASSEMBLY="emr-assembly-0.1.jar"
BUCKET=$1

function trap_handler()
{
        MYSELF="$0"
        LASTLINE="$1"
        LASTERR="$2"
        echo "${MYSELF}: line ${LASTLINE}: exit status of last command: ${LASTERR}"
}

if [ -z "$1" ]; then
  echo "Usage: start.sh  <bucket-name>"
  exit 1;
fi

set -e
trap 'trap_handler ${LINENO} $?' ERR

echo "#################### COPYING ASSEMBLY ###################"
aws s3 cp s3://$BUCKET/$ASSEMBLY ../
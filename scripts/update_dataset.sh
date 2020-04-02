#!/bin/bash

function trap_handler()
{
        MYSELF="$0"
        LASTLINE="$1"
        LASTERR="$2"
        echo "${MYSELF}: line ${LASTLINE}: exit status of last command: ${LASTERR}"
}


set -e

trap 'trap_handler ${LINENO} $?' ERR

BUCKET=$1

if [ -z "$1" ]; then
  echo "Usage: update_dataset.sh  <bucket-name>"
  exit 1;
fi

command -v "aws" >/dev/null 2>&1
if [[ $? -ne 0 ]]; then
    echo "I require aws but it's not installed. Abort."
    exit 1
fi

echo "#################### GETTING DATASET ########################"
wget https://www.bondora.com/marketing/media/LoanData.zip -O temp.zip; unzip temp.zip; rm temp.zip

echo "#################### COPYING DATASET ########################"
aws s3 cp LoanData.csv s3://$BUCKET; rm LoanData.csv
[![BCH compliance](https://bettercodehub.com/edge/badge/AdamF42/BondoraCreditRiskPrevision?branch=master&token=0f8b85c0bbfa146d2313a1100e9d92b6b06bb51a)](https://bettercodehub.com/)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8219c8cea7be4ff9813fe8a93b50aac0)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AdamF42/BondoraCreditRiskPrevision&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/AdamF42/BondoraCreditRiskPrevision.svg?branch=master)](https://travis-ci.org/AdamF42/BondoraCreditRiskPrevision)
# BondoraCreditRiskPrevision


## Introduction

Peer-to-peer (P2P) lending is a form of online micro-financing that has been growing as an alternative to traditional credit financing. P2P lending allows individuals to lend or borrow directly from each other without financial intermediaries, through an internet based platform.

Credit scoring is defined as a statistical method used to predict the creditworthiness of a customer, i.e. to estimate whether a loan will default or succeed.

The aim of this project is to use neural networks to solve the problem of credit scoring with 

The dataset used in the project can be downloaded freely from the Loan dataset of Bondora (https://www.bondora.com/it/public-reports)

## Development Environment

You need to install:
- sbt v1.3.8
- spark v2.4.4 with hadoop
- node v13.12.0

### EMR

### EC2
   
## Execution on aws services

### EMR
   1. Firstly set aws credentials copying and pasting aws_access_key_id and aws_secret_access_key the following into ~/.aws/credentials. For educate accounts it is necessary to copy even aws_session_token.
   2. Execute the script update_dataset.sh with the command
      ./update_dataset.sh <buket-name>
      This command allows you to download latest version of Bondora Loan Dataset. 
   3. Execute the script build_and_deploy.sh with the command
      ./build_and_deploy.sh <buket-name> 
      This executable is used to built the project, run tests, create jar files of the project and deploy them to the bucket passed as parameter. 
   4. Execute the script start_cluster.sh with the command
      ./start_cluster.sh<pem-keys> <bucket-name> <subnet>
      This executable creates an emr cluster with 1 master and 3 slaves of type m5.xlarge

### EC2


### Tips

(1) How to create an s3 bucket
Go to Amazon S3 console (https://s3.console.aws.amazon.com/s3)
Click on “Create bucket”
Choose a name for the bucket and click on the button “Create”

(2) How to create an EC2 key pair
Go to AWS Management Console (https://console.aws.amazon.com/ec2/home)
In the Navigation pane, click Key Pairs
On the Key Pairs page, click Create Key Pair
In the Create Key Pair dialog box, enter a name for your key pair, such as, mykeypair
Click Create
Save the resulting PEM file in a safe location

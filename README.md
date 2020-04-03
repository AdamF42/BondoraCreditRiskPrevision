[![BCH compliance](https://bettercodehub.com/edge/badge/AdamF42/BondoraCreditRiskPrevision?branch=master&token=0f8b85c0bbfa146d2313a1100e9d92b6b06bb51a)](https://bettercodehub.com/)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/8219c8cea7be4ff9813fe8a93b50aac0)](https://www.codacy.com?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=AdamF42/BondoraCreditRiskPrevision&amp;utm_campaign=Badge_Grade)
[![Build Status](https://travis-ci.org/AdamF42/BondoraCreditRiskPrevision.svg?branch=master)](https://travis-ci.org/AdamF42/BondoraCreditRiskPrevision)
# BondoraCreditRiskPrevision


## Introduction

Peer-to-peer (P2P) lending is a form of online micro-financing that has been growing as an alternative to traditional credit financing. P2P lending allows individuals to lend or borrow directly from each other without financial intermediaries, through an internet based platform.

Credit scoring is defined as a statistical method used to predict the creditworthiness of a customer, i.e. to estimate whether a loan will default or succeed.

The aim of this project is to use neural networks to solve the problem of credit scoring.
In this project are tested performance of Multilayer Perceptron Classifier (MLP) and Random Forest Classifier
(RF)

The dataset used in the project can be downloaded freely from the Loan dataset of Bondora (https://www.bondora.com/it/public-reports). To download it from command line, type the following commands in project folder:
```
wget https://www.bondora.com/marketing/media/LoanData.zip -O ../Bondora.zip
unzip ../Bondora.zip -d ../
```
## Development Environment

You need to install:
- sbt v1.3.8
- spark v2.4.4 with hadoop
- node v13.12.0

## Local execution

### EMR
1. First of all, build the project running the command   
    ```
    sbt clean assembly
    ```   
2. Run the *emr-assembly-0.1.jar* with the command  
    ```
    spark-submit emr/target/scala-2.11/emr-assembly-0.1.jar   
    ```  
    This command will train an MLP and an RF model on 60% of Bondora Dataset and will test it on the remaining part of the Dataset. In output it will display the accuracy results of the testing phase of each model.          

### EC2
1. First you need to turn on server node with the command
    ```
    node ./node/app.js
    ```  
    That application is a simulation of what can be received by Bondora API
2. Run the *ec2-assembly-0.1.jar* with the command
    ```
    spark-submit ec2/target/scala-2.11/ec2-assembly-0.1.jar
    ```
    When the server is ready to receive requests, it will display the message “Started”
3. Then send a request to the server putting on a browser the following path  
   ```
    localhost:9000/data
   ```
    The result of classifying the new Bondora loans would be displayed in JSON format

   
## Execution on aws services

### EMR
1. Firstly set aws credentials copying and pasting **aws_access_key_id** and **aws_secret_access_key** into *~/.aws/credentials*. For educate accounts it is necessary to copy even **aws_session_token**.
2. Execute the script *update_dataset.sh* with the command
    ```
    ./update_dataset.sh <buket-name>
    ```
    This command allows you to download latest version of Bondora Loan Dataset.
3. Execute the script *build_and_deploy.sh* with the command
    ```
    ./build_and_deploy.sh <buket-name>
    ```
    This executable is used to built the project, run tests, create jar files of the project and deploy them to the bucket passed as parameter.
4. Execute the script *start_cluster.sh* with the command
    ```
    ./start_cluster.sh<pem-keys> <bucket-name> <subnet>
    ```
    This executable creates an emr cluster with 1 master and 3 slaves of type m5.xlarge

### EC2

1. Create an EC2 Instance and set security group to accept HTTP requests (for further information go to "Tips")
2. Copy set up file for EC2 *ec2_setup.sh* to your machine
    ```   
    scp -i <pem-keys> ./scripts/ec2_setup.sh root@my.ec2.id.amazonaws.com:/<ec2-home>
    scp -i <pem-keys> ./node/app.js root@my.ec2.id.amazonaws.com:/<ec2-home>
    ```
3. Access via ssh to EC2 machine 
    ```   
    ssh <pem-keys> root@my.ec2.id.amazonaws.com
    ```
4. Execute ec2_setup.sh file
    ```   
    sudo ./ec2_setup.sh
    ```   
5. Set aws credentials copying and pasting aws_access_key_id and aws_secret_access_key into ~/.aws/credentials. For educate accounts it is necessary to copy even aws_session_token.
    ```   
    mkdir .aws
    nano ~/.aws/credentials 
    #paste credentials inside that file
    ```   
6. Copy ec2-assembly-0.1.jar file from s3 to ec2
    ```   
    aws s3 cp s3://<bucket-name>/ec2-assembly-0.1.jar .
    ```  
7. Turn on server node with the command
    ```
    node ./node/app.js
    ```  
    That application is a simulation of what can be received by Bondora API
8. Run the ec2-assembly-0.1.jar with the command
    ```
    sudo /opt/spark-2.4.4-bin-hadoop2.7/bin/spark-submit ec2-assembly-0.1.jar s3://<bucket-name>
    ```
    When the server is ready to receive requests, it will display the message “Started”
9. Then send a request to the server putting on a browser the following path   
   ```
    <your-machine-ip>:80/data
   ```
    The result of classifying the new Bondora loans would be displayed in JSON format   


### Tips

####How to create an s3 bucket
+ Go to Amazon S3 console (https://s3.console.aws.amazon.com/s3)
+ Click on “Create bucket”
+ Choose a name for the bucket and click on the button “Create”

####How to create an EC2 key pair
+ Go to AWS Management Console (https://console.aws.amazon.com/ec2/home)
+ In the Navigation pane, click Key Pairs
+ On the Key Pairs page, click Create Key Pair
+ In the Create Key Pair dialog box, enter a name for your key pair, such as, mykeypair
+ Click Create
+ Save the resulting PEM file in a safe location

####How to create EC2 Instance
+ From AWS Management Console choose "EC2"
+ Click on "Running Instances" and then on "Launch Instance"
+ Select an Amazon Machine Image e.g. Ubuntu Server 18.04 LTS (HVM), SSD Volume Type
+ Click on "Review and Launch" and then on "Launch"
+ Select a Key Pair and click on "Launch Instances"

####Change security group for EC2 Instance
+ Search for the label "Security group" and click on the link with security name (ex. launch-wizard-n)
+ Click on the the security group link
+ Click on "Edit inbound rules" and then on "Add Rule"
+ Add the following rule:
    + Type: Custom TCP
    + Protocol: TCP
    + Port range: 9000
    + Source: Anywhere
 + Click on "Save" button
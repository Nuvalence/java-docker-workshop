# Document Storage using S3 and Dynamo Workshop
In this workshop, we will write Java code to save objects to S3 and metadata about 
those objects to DynamoDB. The workshop assumes that you have an AWS account with
the required credentials for interacting with S3 and DynamoDB, and that those 
credentials are configured on your workstation.

## Problem Statement
Our solution must meet the following requirements/specifications:
1. We intend to store documents in S3 that contain a message with the current time
in milliseconds of the format `The current time in milliseconds is {current time}`
2. When we store a document, we will create a new user key that will be a UUID and 
associate that user UUID with the object in S3
3. We need to be able to look up an S3 object key by a user UUID

## Creating the S3 Bucket
First we will create an S3 bucket to store our documents
1. In the AWS console, navigate to the S3 service
2. Click "Create Bucket"
3. Create a bucket named `nuvalence-workshop-{your name}`
4. Leave the rest of the default settings (No encryption, no public access, no versioning)

## Creating the DynamoDB Table
Next we will create a table for our object metadata
1. In the AWS console, navigate to the DynamoDB service
2. Click "Create Table"
3. Specify `nuvalence-workshop-{your name}` as the table name
4. Specify `associatedUser` as the partition key with type `String`
5. Click the checkbox to add a sort key
6. Specify `objectKey` as the sort key with type `String`
7. Uncheck the box for "Use default settings"
8. Under "Auto Scaling", uncheck the box for both "Read capacity" and "Write capacity"
9. Under "Provisioned capacity", set the RCU and WCU both to 1

__CHECKPOINT - If you do not have an S3 bucket and DynamoDB, go back and start from the top of this workshop__

 
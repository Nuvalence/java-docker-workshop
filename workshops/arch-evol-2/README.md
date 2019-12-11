# Document Storage using S3 and Dynamo Workshop
In this workshop, we will deploy an http API to ECS and proxy to the endpoint in the container
using API Gateway. This will also involve the creation of an Application Load Balancer.

## Problem statement
Let's assume we have an http app that has two endpoints:
* A healthcheck endpoint that is a `GET` at `/` (the root resource) which should return `ok` and a response code of 200
* An endpoint that accepts payloads of the form `{"content":"Hello World"}` which is a `POST` to `/document`
Our goal is to put this app in a container to be able to run it on Fargate in ECS

## Initial Setup
1. Copy the [Dockerfile](Dockerfile) in this directory to the root of the repository
2. Replace the environment variables in the Dockerfile with your table name and bucket name from the previous exercise
3. Run `gradlew clean build shadowJar` at the root of the repo to ensure you have the most recent version of the app 

## Pushing image to ECR
1. Create a new ECR repository named "{yourname}/workshop-api"
2. Follow the push commands to build and push your image to ECR

## Creating a Task Definition
1. In ECS, click Task Definitions
2. Click create new and select Fargate
3. Name the definition "nuvalence-docs-app"
4. Select the ecsTaskExecutionRole
5. configure memory and cpu
6. add a container
7. name the container "docs-api"
8. specify the image 
9. add a port mapping for 4567
10. click create

## Creating the ALB
1. go to EC2
2. create new load balancer
3. select app load balancer
4. name
5. internet facing
6. ipv4
7. create a new SG
8. name excelsior-alb-sg
9. allow tcp on 80
10. new target group
11. name excelsior-tg
12. ip type
13. http protocol
14. port 4567
15. skip register targets
16. create

## Creating a service
1. for your task def, click actions -> create service
2. launch type fargate
3. service name "{yourname}-docs-service"
4. 2 task for num tasks
5. click edit button on security group
6. allow custom tcp on 4567 from anywhere
7. select application load balancer
8. select lb created
9. select tg created
10. uncheck the service discovery check box

## Attaching policies for S3 and Dynamo Access
1. Attach AmazonDynamoDBFullAccess and AmazonS3FullAccess to the ecsTaskExecution role

test the endpoint through the alb

curl -XPOST http://excelsior-alb-1591154216.us-east-1.elb.amazonaws.com/document -d '{"content":"Hello ECS"}'

## Creating API Gateway
1. create new regional api with name {yourname}-docs-api
2. create a new resource
3. check box for proxy resource
4. enter the url for the lb followed by /{proxy}
5. click actions
6. deploy api
7. select [New Stage]
8. stage name dev
9. use the invoke url to test your endpoint
10. curl -XPOST https://j0c34dnqd9.execute-api.us-east-1.amazonaws.com/dev/document -d '{"content":"Hello API Gateway"}'



 
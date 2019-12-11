# Document Storage using S3 and Dynamo Workshop
In this workshop, we will deploy an API to ECS and proxy to the endpoint in the container
using API Gateway.

## Problem statement
Http app
healthcheck at `/`
`POST to /document` with payload `{"content":"Hello World"}`

## Pushing image to ECR
1. Create a new ECR repository named {your name}/workshop-api
2. Follow the push commands to build and push your image to ECR

## Creating a Task Definition
1. In ECS, click Task Definitions
2. Click create new and select Fargate
3. Name the definition "nuvalence-docs-app"
4. Select the ecsTaskExecutionRole
configure memory and cpu
add a container
name the container "docs-api"
specify the image 
add a port mapping for 4567
click create

## Creating the ALB
go to EC2
create new load balancer
select app load balancer
name
internet facing
ipv4
create a new SG
name excelsior-alb-sg
allow tcp on 80
new target group
name excelsior-tg
ip
http protocol
port 4567
skip register targets
create

## Creating a service
for your task def, click actions -> create service
launch type fargate
service name "{yourname}-docs-service"
1 task for num tasks
click edit button on security group
allow custom tcp on 4567 from anywhere
select application load balancer
select lb created
select tg created
uncheck the service discovery check box

## Attaching policies for S3 and Dynamo Access
Attach AmazonDynamoDBFullAccess and AmazonS3FullAccess to the ecsTaskExecution role

test the endpoint through the alb
curl -XPOST http://excelsior-alb-1591154216.us-east-1.elb.amazonaws.com/document -d '{"content":"Hello ECS"}'

## Creating API Gateway
create new regional api with name {}
create a new resource
check box for proxy resource
enter the url for the lb followed by /{proxy}
click actions
deploy api
[New Stage]
stage name dev
use the invoke url to test your endpoint
curl -XPOST https://j0c34dnqd9.execute-api.us-east-1.amazonaws.com/dev/document -d '{"content":"Hello API Gateway"}'



 
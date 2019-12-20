# Document Storage using S3 and Dynamo Workshop
In this workshop, we will deploy an HTTP API to ECS and proxy to the endpoint in the container
using API Gateway. This will also involve the creation of an Application Load Balancer.

## Problem statement
Now that we have code to write to both Dynamo and S3, we are going to create an http application that has two endpoints:
* A healthcheck endpoint that is a `GET` at `/` (the root resource) which should return `ok` and a response code of 200 (we'll use this as a healthcheck)
* An endpoint that accepts payloads of the form `{"content":"Hello World"}` which is a `POST` to `/document`
Our goal is to put this app in a container to be able to run it on Fargate in ECS

## Creating the DocumentAPI class
You can copy the example solution from the workshop directory or begin with the empty base provided and try to implement it yourself!
For the http layer we will use the [sparkjava framework](http://sparkjava.com/documentation)

Let's now begin building our http wrapper code for the existing logic. This code is a bit different than the code from the previous session.
The initial implementation outputted a string to the file in S3 with the timestamp of when it was generated, in this case, we will read the contents of the POST request and placed that in the file instead. 

**Creating the root resource healthcheck endpoint**

The first endpoint we will create is a `GET` to the root resource `/`. The resource should return "ok" with a status of 200
```java
get("/", (req, res) -> {
    return "ok";
});
```

**Creating the document endpoint**

We'll start by creating an empty route with no functionality to outline the method and path
```java
post("/document", (req, res) -> {
    // our logic will go here
});
``` 

Next, we need to parse the body of the request so that we can pull out our `content` string that will end up in our document
```java
post("/document", (req, res) -> {
    // parse the req.body() using a Jackson object mapper to deserialize the json payload to a map
    Map<String, String> body = MAPPER.readValue(req.body(), new TypeReference<Map<String, String>>() {});
});
```

Now that we have a map, we can get the content using the content key and store the content using our existing application logic
```java
post("/document", (req, res) -> {
    Map<String, String> body = MAPPER.readValue(req.body(), new TypeReference<Map<String, String>>() {
    });
    String content = body.get("content"); // retrieve the value for the content key from the map
    UUID userId = new DocumentAPI().store(content); // use our existing functionality to store the content
    return userId.toString(); // return the user GUID as a string to the caller
});
```

At this point we have functioning, happy path code. However, if we run into any issues we want to return a failure message to the caller.
```java
try {
   Map<String, String> body = MAPPER.readValue(req.body(), new TypeReference<Map<String, String>>() {});
   String content = body.get("content");
   UUID userId = new DocumentAPI().store(content);
   return userId.toString();
} catch (Exception e) { // add a blanket catch clause
    e.printStackTrace();
     res.status(500);
     return "There was an error processing your POST request";
}
```

**Running the application**

There are a few things you need to do in order to run the solution. If you started with the empty base class, start on step 1, otherwise go to step 2.

1. Update the Workshops.java class to call `DocumentAPIBase` instead of `DocumentAPI` when passing the `docsapi` argument.
2. Build your project using `./gradlew clean build shadowJar` at the root of your source
3. Configure the environment variables with the name of your S3 bucket and DynamoDB table (BUCKET_NAME and TABLE_NAME)
4. Run your application and remember to pass `docsapi` as an argument 


## Post-implementation Docker setup
1. Copy the [Dockerfile](Dockerfile) in this directory to the root of the repository
2. Replace the environment variables in the Dockerfile with your table name and bucket name from the previous exercise
3. Run `gradlew clean build shadowJar` at the root of the repo to ensure you have the most recent version of the app

## Pushing image to ECR
1. Create a new ECR repository named `{yourname}/workshop-api`
2. Follow the push commands to build and push your image to ECR

## Creating a Task Definition
1. In ECS, click Task Definitions
2. Click create new, and select Fargate
3. Name the definition `nuvalence-docs-app-{yourname}`
4. Select the ecsTaskExecutionRole as the task execution role
5. Configure memory and cpu (the lowest settings will suffice)
6. Click the "Add container" button
7. Name the container `docs-api`
8. The image should be the full image name (including repository) that you pushed in the previous set of steps 
9. Add the port 4567 to be mapped in the container
10. Click create to create the task definition

## Creating the Application Load Balancer
1. Within the console, navigate to the EC2 service
2. Scroll down to Load Balancers on the left, and click create a new load balancer
3. Select the Application Load Balancer type
4. Enter a name for your load balancer `nuvalence-workshop-lb-{yourname}`
5. Select "internet facing" for the scheme and the "ipv4" address type
6. Use the default listener for http on port 80
7. Select the default VPC and at least 2 subnets 
8. Click through the next screen to create a new *Security Group* with the name `nuvalence-alb-{yourname}-sg`
9. Ensure there is a single rule to allow TCP from anywhere on port 80
10. Click next and then create a new target group
11. Name the target group `nuvalence-{yourname}-tg`
12. Select the IP target type
13. Leave the http protocol on port 80 
14. The healthcheck configuration can also be left as-is
15. Click next, and _do not_ register targets
16. Create your ALB

## Creating a service
1. Navigate to the ECS console and find your Task Definition, click "Actions" and select "Create Service"
2. Select "Fargate" as the launch type
3. Specify a service name of `{yourname}-docs-service`
4. For the number of tasks, enter 2
5. Click the edit button on the Security Group
6. Add a rule to allow custom tcp on port 4567 from anywhere
7. Enable load balancing and select the application load balancer you created in the previous steps
8. Select the target group you created in the previous steps
9. Uncheck the service discovery check box, and create the service

## Attaching policies for S3 and Dynamo Access
In order for your ECS task to be able to access Dynamo and S3, we need to add a few policies to the task execution role
1. Attach _AmazonDynamoDBFullAccess_ and _AmazonS3FullAccess_ to the ecsTaskExecution role

**CHECKPOINT** test the endpoint through the ALB to ensure everything is properly configured
```shell
curl -XPOST http://{ALB hostname}/document -d '{"content":"Hello ECS"}'
```

## Creating API Gateway
The final step is to create an API Gateway instance to proxy through to our ECS service
1. Create a new regional api with name `{yourname}-docs-api`
2. Create a new resource for your api
3. Check the box to make the resource a `proxy resource`. Leave the defaults and click *Create Resource*. 
4. Select *HTTP Proxy* as the integration type. Enter the URL for the ALB followed by `/{proxy}` as the endpoint URL. 
5. On your API, click the *Actions* dropdown button and then select "Deploy API"
6. Select [New Stage] to create a new stage for the deployment
7. Supply a stage name of `dev`
8. Once the API has been deployed, you can use the invoke url to test your endpoint

```shell
curl -XPOST https://{invoke url}/document -d '{"content":"Hello API Gateway"}'
```

Celebrate!



 
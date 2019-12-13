# Document Lookup using Lambda, S3 and Dynamo Workshop
In this workshop, we will write Java code to execute in a Lambda which will 
lookup object keys in Dynamo by user GUID, and then retrieve the document content
from an S3 bucket.

## Problem Statement
Now that we have code to write to both Dynamo and S3 behind an HTTP API, we are going to create a Lambda which will
sit behind a new endpoint to `GET /content/{userGUID}`
* The `{userGUID}` will be a path parameter that we'll replace with an actual GUID
* For example we would `GET` `/content/00e8c889-9fa2-4123-bc0b-c26f32c9e618`

## Creating the DocumentLookupLambda class
You can follow along the already built class or begin with the empty base `DocumentLookupLambdaBase` provided and try to implement it yourself!

Let's now begin building our Lambda code to query Dynamo and get objects from S3.

**Looking up the object key in DynamoDB**

First we need to access the path parameter for the user GUID
```java
String userGUIDString = input.getPathParameters().get("userGUID");
System.out.println(userGUIDString);
``` 

Next we need to create a `Table` object to be able to query our Dynamo table
```java
Table table = dynamoDB.getTable(tableName);
```

Next we need to setup our `QuerySpec` which will define our query against our table. This is somewhat complicated syntax that is best learned 
through documentation and trial and error, but a solution is supplied here.
```java
 // Create a map of named attributes
HashMap<String, String> nameMap = new HashMap<String, String>();
nameMap.put("#u", "associatedUser");

// Create a map of values which will be substituted in the key condition expression
HashMap<String, Object> valueMap = new HashMap<String, Object>();
valueMap.put(":userGUID", userGUIDString);

// Create a query spec object with a projection expression for the user guid and the object key
// Use a key condition expression to find items by the user GUID
QuerySpec querySpec = new QuerySpec()
        .withProjectionExpression("#u, objectKey")
        .withKeyConditionExpression("#u = :userGUID")
        .withNameMap(nameMap)
        .withValueMap(valueMap);
```

For the last step in querying Dynamo, we need to execute the query and find the first item in the result. Then we will extract the object key from 
the item.
```java
// Query the table using the QuerySpec and capture the resulting collection of items
ItemCollection<QueryOutcome> items = table.query(querySpec);

// Iterate through the items and get the first object key
Iterator<Item> iterator = items.iterator();
String objectKey = null;
if (iterator.hasNext()) {
    Item item = iterator.next();
    objectKey = item.getString("objectKey");
}
```

**Getting the object from S3**

First we need to get the object from our S3 bucket.
```java
// Use the S3 client to retrieve the object from the bucket
S3Object s3Object = s3Client.getObject(bucketName, objectKey);
S3ObjectInputStream inputStream = s3Object.getObjectContent();
```

Finally, we will create an instance of the API Gateway proxy result and pass back the response to the caller.
```java
 String documentContent = displayTextInputStream(inputStream);
APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
responseEvent.setStatusCode(200);
responseEvent.setBody(documentContent);
return responseEvent;
```

*Note that the method `displayTextInputStream(inputStream)` was supplied as a convenience utility*

## Building the zip file to upload to Lambda, and creating the Lambda
Now we need to package our Lambda function so we can upload it via the console.
1. Run `gradlew clean buildZip`
2. In the Lambda console, click the button to "Create function"
3. Name your function `{yourname}-document-retrieval`
4. Select a runtime of Java 8
5. Choose an existing execution role
6. Under "function code", upload the zip file that was generated in the `build/distributions` directory
7. Set the handler to `io.nuvalence.workshops.DocumentLookupLambda` or `io.nuvalence.workshops.DocumentLookupLambdaBase` depending on what path you chose to follow
8. Set environment variables for your function for `BUCKET_NAME` and `TABLE_NAME`

## Creating API Gateway Resource
The final step is to create an API Gateway instance to call our Lambda function. *NOTE - if you still have your API Gateway from the previous workshop, you can use that*
1. Create a new regional api with name `{yourname}-docs-api`
2. Create a new resource for your api with the name Content and the path `/content`
3. With the Content resource selected, use the Actions dropdown to create another resource
4. Name the resource ByUserGUID and set the path to `{userGUID}`
5. Add a `GET` method to the ByUserGUID resource
6. Set the integration type to "Lambda Function"
7. Check the box for "Use Lambda Proxy Integration"
8. Start typing the name of your lambda function to select it for user by the method, and click Save
9. On your API, click the *Actions* dropdown button and then select "Deploy API"
10. Select [New Stage] to create a new stage for the deployment
11. Supply a stage name of `dev`
12. Once the API has been deployed, you can use the invoke url to test your endpoint

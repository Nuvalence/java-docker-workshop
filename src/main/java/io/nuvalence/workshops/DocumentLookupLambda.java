package io.nuvalence.workshops;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

public class DocumentLookupLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final DynamoDB dynamoDB;
    private final AmazonS3 s3Client;
    private final String tableName;
    private final String bucketName;

    public DocumentLookupLambda() {
        // Create a new instance of the S3 client, make sure to specify the region
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion("us-east-1")
                .build();

        // Create a new instance of the DynamoDB client
        AmazonDynamoDB dynamo = AmazonDynamoDBClientBuilder.defaultClient();
        dynamoDB = new DynamoDB(dynamo);

        // Two environment variables must be set, the first is BUCKET_NAME, the second is TABLE_NAME
        // The variables can be set using the shell export command
        bucketName = System.getenv("BUCKET_NAME");
        tableName = System.getenv("TABLE_NAME");
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        // Get the path parameter named userGUID
        String userGUIDString = input.getPathParameters().get("userGUID");
        System.out.println(userGUIDString);

        // This is added to trigger a 500 for demo purposes
        if (userGUIDString.equalsIgnoreCase("trigger500"))
        {
            APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
            responseEvent.setStatusCode(500);
            return responseEvent;
        }

        // Get an instance of the Table object to query DynamoDB
        Table table = dynamoDB.getTable(tableName);

        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#u", "associatedUser");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":userGUID", userGUIDString);

        QuerySpec querySpec = new QuerySpec()
                .withProjectionExpression("#u, objectKey")
                .withKeyConditionExpression("#u = :userGUID")
                .withNameMap(nameMap)
                .withValueMap(valueMap);

        ItemCollection<QueryOutcome> items = table.query(querySpec);

        Iterator<Item> iterator = items.iterator();
        String objectKey = null;
        if (iterator.hasNext()) {
            Item item = iterator.next();
            objectKey = item.getString("objectKey");
        }

        if (objectKey == null) {
            APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
            responseEvent.setStatusCode(404);
            return responseEvent;
        }

        S3Object s3Object = s3Client.getObject(bucketName, objectKey);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        try {
            String documentContent = displayTextInputStream(inputStream);
            APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
            responseEvent.setStatusCode(200);
            responseEvent.setBody(documentContent);
            return responseEvent;
        } catch (Exception e) {
            APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
            responseEvent.setStatusCode(500);
            return responseEvent;
        }
    }

    private static String displayTextInputStream(InputStream input) throws IOException {
        // Read the text input stream one line at a time and display each line.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }
}

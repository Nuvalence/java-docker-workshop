package io.nuvalence.workshops;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DocumentLookupLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final AmazonDynamoDB dynamo;
    private final AmazonS3 s3Client;
    private final String tableName;
    private final String bucketName;

    public DocumentLookupLambda() {
        // Create a new instance of the S3 client, make sure to specify the region
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion("us-east-1")
                .build();

        // Create a new instance of the DynamoDB client
        dynamo = AmazonDynamoDBClientBuilder.defaultClient();

        // Two environment variables must be set, the first is BUCKET_NAME, the second is TABLE_NAME
        // The variables can be set using the shell export command
        bucketName = System.getenv("BUCKET_NAME");
        tableName = System.getenv("TABLE_NAME");
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        String userGUIDString = input.getPathParameters().get("userGUID");
        System.out.println(userGUIDString);

        // implement dynamo lookup and s3 retrieval
        QueryRequest queryRequest = new QueryRequest();
        queryRequest.setTableName(tableName);

        queryRequest.setAttributesToGet(Collections.singletonList("objectKey"));
        queryRequest.setKeyConditionExpression("associatedUser=:userGUID");

        Map<String, AttributeValue> expressionValueMap = new HashMap<>();
        expressionValueMap.put("userGUID", new AttributeValue(userGUIDString));

        queryRequest.setExpressionAttributeValues(expressionValueMap);

        QueryResult result = dynamo.query(queryRequest);

        String objectKey = result.getItems().get(0).get("objectKey").getS();
        S3Object s3Object = s3Client.getObject(bucketName, objectKey);

        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        responseEvent.setStatusCode(200);
        responseEvent.setBody(s3Object.getObjectContent().toString());
        return responseEvent;
    }
}

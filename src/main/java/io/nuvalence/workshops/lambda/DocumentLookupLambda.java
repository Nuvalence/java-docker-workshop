package io.nuvalence.workshops.lambda;

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
        // Create a new instance of the API used for easier querying of DynamoDB
        dynamoDB = new DynamoDB(dynamo);

        // Two environment variables must be set, the first is BUCKET_NAME, the second is TABLE_NAME
        // The variables can be set using the shell export command
        bucketName = System.getenv("BUCKET_NAME");
        tableName = System.getenv("TABLE_NAME");
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        // Get the path parameter named userGUID using the path parameters on the input

        // Get an instance of the Table object to query your DynamoDB

        // Create a map of named attributes


        // Create a map of values which will be substituted in the key condition expression


        // Create a query spec object with a projection expression for the user guid and the object key
        // Use a key condition expression to find items by the user GUID


        // Query the table using the QuerySpec and capture the resulting collection of items

        // Iterate through the items and get the first object key


        // Return a 404 if the object key is null


        // Use the S3 client to retrieve the object from the bucket


        // Use the inputstream to text helper method to return an APIGatewayProxyResponseEvent with text body and response code 200

        // Make sure to handle any exceptions that occur reading the stream and return a 500 status code

        return null;
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

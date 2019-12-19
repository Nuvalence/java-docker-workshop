package io.nuvalence.workshops;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;
import com.amazonaws.services.kms.model.NotFoundException;
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
import java.util.UUID;

public class DocumentLookupLambdaRefactor implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


    private final DocumentRetriever documentRetriever;

    public DocumentLookupLambdaRefactor()
    {
        documentRetriever = new DocumentRetriever(new S3FileRepository(), new DynamoDBDocumentStoreRepository());
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        // Get the path parameter named userGUID


        try {
            String userGUIDString = input.getPathParameters().get("userGUID");
            System.out.println(userGUIDString);
            String fileContent = documentRetriever.retrieveFileContentsForUser(userGUIDString);
            return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(fileContent);
        }
        catch (NotFoundException ex) {
            return new APIGatewayProxyResponseEvent().withStatusCode(404).withBody("No file was found for the requested user");
        }
        catch (Throwable t) {
            return new APIGatewayProxyResponseEvent().withStatusCode(500).withBody("An unexpected error occurred retrieving the file");
        }

    }




}

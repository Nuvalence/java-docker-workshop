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

public class DocumentLookupLambdaRefactor implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final FileRepository objectRepository;
    private final DocumentStoreRepository documentStoreRepository;

    public DocumentLookupLambdaRefactor(FileRepository objectRepository, DocumentStoreRepository documentRepository) {
        if(null != objectRepository) {
            this.objectRepository = objectRepository;
        }
        else {
            this.objectRepository = new S3FileRepository();
        }

        if(null != documentRepository) {
            this.documentStoreRepository = documentRepository;
        }
        else {
            this.documentStoreRepository = new DynamoDBDocumentStoreRepository();
        }

    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        // Get the path parameter named userGUID
        String userGUIDString = input.getPathParameters().get("userGUID");
        System.out.println(userGUIDString);

        String objectKey = documentStoreRepository.getObjectKey(userGUIDString);


        if (objectKey == null) {
            APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
            responseEvent.setStatusCode(404);
            return responseEvent;
        }



        try {
            String documentContent = objectRepository.RetrieveFileContent(objectKey);
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


}

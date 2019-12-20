package io.nuvalence.workshops;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringInputStream;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;

public class DocumentStorageWorkshop {

    private final AmazonDynamoDB dynamo;
    private final AmazonS3 s3Client;
    private final String tableName;
    private final String bucketName;

    public DocumentStorageWorkshop() {
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

    public void run() throws Exception {
        // Create the key information we want to store
        final long time = System.currentTimeMillis(); // the current time in milliseconds
        final UUID userGUID = UUID.randomUUID(); // a user GUID we can store in Dynamo
        final String objectKey = "document/" + time + ".txt"; // an object key to use for storing our S3 object

        // Create a new metadata object for our S3 request (NOTE - this is different from our document metadata which is a business requirement)

        // Create InputStream object representing the content of our file

        // Create a put object request and use the S3 client to put our object into S3 at the specified key

        // Start building our item we want to insert into DynamoDB.

        //Insert values into item. This item will hold three values, the UUID, the time and the objectKey. 

        //Put item in DynamoDB table

        // Print out the current state of both the bucket and the table

    }
}

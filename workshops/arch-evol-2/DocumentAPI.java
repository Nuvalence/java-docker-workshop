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
import com.amazonaws.util.json.Jackson;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.*;

import static spark.Spark.*;

public class DocumentAPI {

    private final AmazonDynamoDB dynamo;
    private final AmazonS3 s3Client;
    private final String tableName;
    private final String bucketName;

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void run() {
        get("/", (req, res) -> {
            return "ok";
        });
        post("/document", (req, res) -> {
            try {
                Map<String, String> body = MAPPER.readValue(req.body(), new TypeReference<Map<String, String>>() {
                });
                UUID userId = new DocumentAPI().store(body.get("content"));
                return userId.toString();
            } catch (Exception e) {
                e.printStackTrace();
                res.status(500);
               return "There was an error processing your POST request";
            }
        });
    }

    public DocumentAPI() {
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

    public UUID store(String documentContent) throws Exception {
        // Create the key information we want to store
        final long time = System.currentTimeMillis(); // the current time in milliseconds
        final UUID userGUID = UUID.randomUUID(); // a user GUID we can store in Dynamo
        final String objectKey = "document/" + time + ".txt"; // an object key to use for storing our S3 object

        // Create a new metadata object for our S3 request (NOTE - this is different from our document metadata which is a business requirement)
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("plain/text"); // set the content type
        metadata.addUserMetadata("x-amz-meta-title", "Hello"); // set a title for the file

        // Create InputStream object representing the content of our file
        InputStream stream = new StringInputStream(documentContent);

        // Create a put object request and use the S3 client to put our object into S3 at the specified key
        PutObjectRequest request = new PutObjectRequest(bucketName, objectKey, stream, metadata);
        s3Client.putObject(request);

        System.out.println("Successfully put object in S3 at key " + objectKey);

        // Start building our item we want to insert into DynamoDB
        HashMap<String, AttributeValue> item = new HashMap<>();

        //Insert values into item
        item.put("associatedUser", new AttributeValue(userGUID.toString()));
        item.put("objectKey", new AttributeValue(objectKey));
        item.put("date", new AttributeValue().withN(Long.toString(time)));

        //Put item in DynamoDB table
        System.out.println("Adding new item...");
        this.dynamo.putItem(this.tableName, item);
        System.out.println("Success! Added item to Dynamo for user " + userGUID + " and object key " + objectKey);

        // Print out the current state of both the bucket and the table
        System.out.println("Current bucket contents:");
        ListObjectsV2Result result = s3Client.listObjectsV2(bucketName);
        result.getObjectSummaries()
                .stream()
                .map(S3ObjectSummary::getKey)
                .forEach(System.out::println);

        System.out.println("Current table contents:");
        ScanResult dbresult = dynamo.scan(tableName, Arrays.asList("associatedUser", "objectKey", "date"));
        dbresult.getItems().stream()
                .map(m -> String.format("%s - %s - %s", m.get("associatedUser").getS(), m.get("objectKey").getS(), m.get("date").getN()))
                .forEach(System.out::println);

        return userGUID;
    }
}

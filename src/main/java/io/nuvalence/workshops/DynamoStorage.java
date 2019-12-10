package io.nuvalence.workshops;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import io.nuvalence.workshops.models.DocumentMetadataModel;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DynamoStorage {
    private final AmazonDynamoDB dynamo;
    private final String tableName;

    public DynamoStorage() {
        //Use environment variable to configure table name
        this(AmazonDynamoDBClientBuilder.defaultClient(), System.getenv("TABLE_NAME"));
    }

    DynamoStorage(AmazonDynamoDB dynamoConnection, String table) {
        this.dynamo = dynamoConnection;
        this.tableName = table;
    }

    public void writeToTable(DocumentMetadataModel data) {
        //Create item to put in DynamoDB
        HashMap<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        long timestamp = System.currentTimeMillis();

        //Insert values into item
        item.put("associatedUser", new AttributeValue(data.getAssociatedUser()));
        item.put("objectKey", new AttributeValue(data.getObjectKey()));
        item.put("date", new AttributeValue().withN(Long.toString(timestamp)));

        //Put item in DynamoDB table
        System.out.println("Adding new item...");
        this.dynamo.putItem(this.tableName, item);
        System.out.println("Success!");
    }

    public List<String> listItems() {
        ScanResult result = dynamo.scan(tableName, Arrays.asList("associatedUser", "objectKey", "date"));
        return result.getItems().stream()
                .map(m -> String.format("%s - %s - %s", m.get("associatedUser").getS(), m.get("objectKey").getS(), m.get("date").getN()))
                .collect(Collectors.toList());
    }
}
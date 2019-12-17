package io.nuvalence.workshops;


import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.QuerySpec;

import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

public class DynamoDBDocumentStoreRepository implements DocumentStoreRepository {

    private final DynamoDB dynamoDB;
    private final String tableName;

    public DynamoDBDocumentStoreRepository()
    {
        AmazonDynamoDB dynamo = AmazonDynamoDBClientBuilder.defaultClient();
        dynamoDB = new DynamoDB(dynamo);

        // Two environment variables must be set, the first is BUCKET_NAME, the second is TABLE_NAME
        // The variables can be set using the shell export command
        tableName = System.getenv("TABLE_NAME");
    }

    @Override
    public String getObjectKey(String userGuid) {
        Table table = dynamoDB.getTable(tableName);

        HashMap<String, String> nameMap = new HashMap<String, String>();
        nameMap.put("#u", "associatedUser");

        HashMap<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put(":userGUID", userGuid);

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

        return objectKey;

    }
}

package io.nuvalence.workshops;

import io.nuvalence.workshops.models.DocumentMetadataModel;

import java.util.UUID;

public class DocumentStorageWorkshop {

    private final S3DocumentStorage s3DocumentStorage;
    private final DynamoStorage dynamoStorage;

    public DocumentStorageWorkshop() {
        s3DocumentStorage = new S3DocumentStorage();
        dynamoStorage = new DynamoStorage();
    }

    public void run() throws Exception {
        DocumentStorageWorkshop workshop = new DocumentStorageWorkshop();
        String key = workshop.s3DocumentStorage.writeToS3();

        DocumentMetadataModel data = new DocumentMetadataModel();
        data.setAssociatedUser(UUID.randomUUID().toString());
        data.setObjectKey(key);

        workshop.dynamoStorage.writeToTable(data);

        workshop.s3DocumentStorage.listObjects().forEach(System.out::println);
        workshop.dynamoStorage.listItems().forEach(System.out::println);
    }
}

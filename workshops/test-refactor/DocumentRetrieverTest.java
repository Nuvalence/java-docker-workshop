package io.nuvalence.workshops.lambda;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

public class DocumentRetrieverTest {

    private final DynamoDBDocumentStoreRepository mockDocumentRepo = Mockito.mock(DynamoDBDocumentStoreRepository.class);
    private final S3FileRepository mockFileRepo = Mockito.mock(S3FileRepository.class);
    private final DocumentRetriever documentRetriever = new DocumentRetriever(mockFileRepo, mockDocumentRepo);

    @Test
    public void retrieveFileContentForUser_GivenUserUUID_ShouldReturnContent() {
        String userGUID = UUID.randomUUID().toString();
        String objectKey = "MyFile";
        String fileContent = "Hello";
        Mockito.when(mockDocumentRepo.getObjectKey(userGUID)).thenReturn(objectKey);
        Mockito.when(mockFileRepo.retrieveFileContent(objectKey)).thenReturn(fileContent);

        String retrievedFileContent = documentRetriever.retrieveFileContentsForUser(userGUID);

        Mockito.verify(mockDocumentRepo).getObjectKey(userGUID);
        Mockito.verify(mockFileRepo).retrieveFileContent(objectKey);
        Assert.assertEquals(fileContent, retrievedFileContent);
    }
}
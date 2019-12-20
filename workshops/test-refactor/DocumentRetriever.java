package io.nuvalence.workshops.lambda;

import com.amazonaws.services.kms.model.NotFoundException;

public class DocumentRetriever {
    private final FileRepository objectRepository;
    private final DocumentStoreRepository documentStoreRepository;

    public DocumentRetriever(FileRepository objectRepository, DocumentStoreRepository documentRepository) {
        this.objectRepository = objectRepository;
        this.documentStoreRepository = documentRepository;
    }

    String retrieveFileContentsForUser(String userGUID) throws NotFoundException {
        String objectKey = documentStoreRepository.getObjectKey(userGUID);
        return objectRepository.retrieveFileContent(objectKey);
    }




}

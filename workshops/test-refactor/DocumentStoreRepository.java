package io.nuvalence.workshops.lambda;

import java.util.UUID;

public interface DocumentStoreRepository {
    String getObjectKey(String userGuid);
}

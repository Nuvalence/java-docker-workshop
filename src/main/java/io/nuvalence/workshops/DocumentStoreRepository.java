package io.nuvalence.workshops;

import java.util.UUID;

public interface DocumentStoreRepository {
    String getObjectKey(String userGuid);
}

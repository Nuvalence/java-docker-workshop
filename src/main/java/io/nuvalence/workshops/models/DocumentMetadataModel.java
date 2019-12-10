package io.nuvalence.workshops.models;

public class DocumentMetadataModel {
    private String objectKey;
    private String associatedUser;

    public String getObjectKey() {
        return this.objectKey;
    }

    public void setObjectKey(String inputName) {
        this.objectKey = inputName;
    }

    public String getAssociatedUser() {
        return this.associatedUser;
    }

    public void setAssociatedUser(String associatedUser) {
        this.associatedUser = associatedUser;
    }
}
package io.nuvalence.workshops;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringInputStream;

import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

public class S3DocumentStorage {
    private final AmazonS3 s3Client;
    private final String bucketName;

    public S3DocumentStorage() {
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion("us-east-1")
                .build();

        bucketName = System.getenv("BUCKET_NAME");
    }

    public String writeToS3() throws Exception {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("plain/text");
        metadata.addUserMetadata("x-amz-meta-title", "Hello");

        long time = System.currentTimeMillis();

        InputStream stream = new StringInputStream("The time is " + time);
        String key = "document/" + time + ".txt";

        PutObjectRequest request = new PutObjectRequest(bucketName, key, stream, metadata);
        s3Client.putObject(request);

        return key;
    }

    public List<String> listObjects() throws Exception {
        ListObjectsV2Result result = s3Client.listObjectsV2(bucketName);
        return result.getObjectSummaries().stream().map(S3ObjectSummary::getKey).collect(Collectors.toList());
    }
}

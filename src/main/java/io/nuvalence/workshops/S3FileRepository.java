package io.nuvalence.workshops;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.*;

public class S3FileRepository implements FileRepository {

    private final AmazonS3 s3Client;
    private final String bucketName;

    public S3FileRepository() {
        s3Client = AmazonS3ClientBuilder.standard()
                .withRegion("us-east-1")
                .build();

        bucketName = System.getenv("BUCKET_NAME");

    }

    @Override
    public String RetrieveFileContent(String fileKey) {
        S3Object s3Object = s3Client.getObject(bucketName, fileKey);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        try{
            String S3StringContent = displayTextInputStream(inputStream);
            return S3StringContent;
        }
        catch (Exception ex)
        {
            System.out.println("There was an error converting the S3 input stream to a String");
            System.out.println(ex.getMessage());
            return null;
        }

    }

    private static String displayTextInputStream(InputStream input) throws IOException {
        // Read the text input stream one line at a time and display each line.
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line);
        }
        return builder.toString();
    }
}

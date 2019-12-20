package io.nuvalence.workshops.lambda;

import com.amazonaws.services.kms.model.NotFoundException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;

public class DocumentLookupLambda implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {


    private final DocumentRetriever documentRetriever;

    public DocumentLookupLambdaRefactor()
    {
        documentRetriever = new DocumentRetriever(new S3FileRepository(), new DynamoDBDocumentStoreRepository());
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        // Get the path parameter named userGUID


        try {
            String userGUIDString = input.getPathParameters().get("userGUID");
            System.out.println(userGUIDString);
            String fileContent = documentRetriever.retrieveFileContentsForUser(userGUIDString);
            return new APIGatewayProxyResponseEvent().withStatusCode(200).withBody(fileContent);
        }
        catch (NotFoundException ex) {
            return new APIGatewayProxyResponseEvent().withStatusCode(404).withBody("No file was found for the requested user");
        }
        catch (Throwable t) {
            return new APIGatewayProxyResponseEvent().withStatusCode(500).withBody("An unexpected error occurred retrieving the file");
        }

    }




}

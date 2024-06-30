package com.myorg.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ParseProductsHandler implements RequestHandler<S3Event, String> {

    @Override
    public String handleRequest(S3Event s3event, Context context) {
        LambdaLogger logger = context.getLogger();

        try {
            S3Client s3Client = S3Client.builder().build();

            for (S3EventNotification.S3EventNotificationRecord record : s3event.getRecords()) {
                String bucketName = record.getS3().getBucket().getName();
                String objectKey = record.getS3().getObject().getKey();

                logger.log("Bucket: " + bucketName);
                logger.log("Key: " + objectKey);

                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build();

                ResponseBytes<GetObjectResponse> objectResponse = s3Client.getObjectAsBytes(getObjectRequest);
                byte[] contentBytes = objectResponse.asByteArray();
                String content = new String(contentBytes, StandardCharsets.UTF_8);

                // Process CSV content
                logger.log("Content: " + content);

                 CSVParser csvParser = CSVParser.parse(new String(contentBytes, StandardCharsets.UTF_8), CSVFormat.DEFAULT.withFirstRecordAsHeader());
                 for (CSVRecord csvRecord : csvParser) {
                     logger.log("Record: " + csvRecord.toString());
                 }
            }

            return "Processing completed";
        } catch (S3Exception e) {
            logger.log("Error fetching object from S3: " + e.getMessage());
            throw new RuntimeException(e); // Optionally rethrow or handle differently based on your application's needs
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

package com.myorg.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import com.google.gson.Gson;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ParseProductsHandler implements RequestHandler<S3Event, String> {

    @Override
    public String handleRequest(S3Event s3event, Context context) {
        LambdaLogger logger = context.getLogger();
        S3Client s3Client = S3Client.builder().build();
        Gson gson = new Gson();

        try {
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

                CSVParser csvParser = CSVParser.parse(content, CSVFormat.DEFAULT.withFirstRecordAsHeader());
                for (CSVRecord csvRecord : csvParser) {
                    Map<String, String> recordMap = new HashMap<>();
                    csvRecord.toMap().forEach(recordMap::put);
                    String jsonRecord = gson.toJson(recordMap);
                    logger.log("Record: " + jsonRecord);
                }

                String newObjectKey = objectKey.replace("uploaded/", "parsed/");
                CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                        .sourceBucket(bucketName)
                        .sourceKey(objectKey)
                        .destinationBucket(bucketName)
                        .destinationKey(newObjectKey)
                        .build();
                s3Client.copyObject(copyObjectRequest);
                logger.log("Copied to: " + newObjectKey);

                DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build();
                s3Client.deleteObject(deleteObjectRequest);
                logger.log("Deleted original object: " + objectKey);
            }

            return "Processing and moving completed";
        } catch (S3Exception e) {
            logger.log("Error processing object from S3: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

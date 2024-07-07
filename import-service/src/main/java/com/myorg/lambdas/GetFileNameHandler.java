package com.myorg.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;
import com.myorg.responses.ApiResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class GetFileNameHandler implements RequestHandler<APIGatewayProxyRequestEvent, ApiResponse> {

    @Override
    public ApiResponse handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        LambdaLogger logger = context.getLogger();

        String fileName = input.getQueryStringParameters().get("name");

        logger.log("Request id: " + context.getAwsRequestId());
        logger.log("Function name: " + context.getFunctionName());
        logger.log("Path parameters: " + input);

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "*");

        if (fileName == null) {
            return ApiResponse.builder()
                    .statusCode(400)
                    .headers(headers)
                    .body("{\"message\": \"Missing 'name' parameter\"}")
                    .build();
        }

        try (S3Presigner presigner = S3Presigner.create()) {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket("file-upload-rs-app")
                    .key("uploaded/" + fileName)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(1))
                    .putObjectRequest(objectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = presigner.presignPutObject(presignRequest);
            String presignedUrl = presignedRequest.url().toExternalForm();

            logger.log("Presigned URL: " + presignedUrl);

            return ApiResponse.builder()
                    .statusCode(200)
                    .headers(headers)
                    .body(presignedUrl)
                    .build();
        } catch (Exception e) {
            logger.log("Exception: " + e.getMessage());
            return ApiResponse.builder()
                    .statusCode(500)
                    .body("{\"error\": \"" + e.getMessage() + "\"}")
                    .build();
        }
    }
}

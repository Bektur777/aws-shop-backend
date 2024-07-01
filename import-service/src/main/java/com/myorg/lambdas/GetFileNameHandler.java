package com.myorg.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.logging.LogLevel;
import com.myorg.responses.ApiResponse;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

public class GetFileNameHandler implements RequestHandler<Map<String, Object>, ApiResponse> {

    @Override
    public ApiResponse handleRequest(Map<String, Object> input, Context context) {
        LambdaLogger logger = context.getLogger();

        Map<String, String> pathParams = (Map<String, String>) input.get("queryStringParameters");

        logger.log("Request id: " + context.getAwsRequestId());
        logger.log("Function name: " + context.getFunctionName());
        logger.log("Path parameters: " + pathParams.toString());

        String fileName = pathParams.get("name");

        if (fileName == null) {
            return ApiResponse.builder()
                    .statusCode(400)
                    .body("{\"message\": \"Missing 'name' parameter\"}")
                    .build();
        }

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET");

        try(S3Presigner presigner = S3Presigner.create()) {
            GetObjectRequest objectRequest = GetObjectRequest.builder()
                    .bucket("file-upload-rs-app")
                    .key("uploaded/" + fileName)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(1))
                    .getObjectRequest(objectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
            System.out.println("Bektur" + presignedRequest.url().toString());
            logger.log("Presigned URL: [{}]" + presignedRequest.url().toString());
            logger.log("HTTP method: [{}]" + presignedRequest.httpRequest().method());
            String presignedUrl = presignedRequest.url().toString();

            System.out.println("==============================================================================");
            System.out.println(presignedUrl);

            return ApiResponse.builder()
                    .statusCode(200)
                    .headers(headers)
                    .body("{\"presignedUrl\": \"" + presignedUrl + "\"}")
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

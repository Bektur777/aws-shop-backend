package com.myorg.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification;
import org.apache.commons.csv.CSVParser;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ParseProductsHandlerTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private Context context;

    private ParseProductsHandler handler;

    @Test
    public void testHandleRequest() {
        String csvContent = "header1,header2\nvalue1,value2";
        ResponseBytes<GetObjectResponse> responseBytes = ResponseBytes.fromByteArray(GetObjectResponse.builder().build(), csvContent.getBytes(StandardCharsets.UTF_8));

        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);

        S3Event s3Event = getS3Event();

        String result = handler.handleRequest(s3Event, context);

        verify(s3Client, times(1)).getObjectAsBytes(any(GetObjectRequest.class));
        assert result.equals("Processing completed");
    }

    private static @NotNull S3Event getS3Event() {
        S3EventNotification.S3EventNotificationRecord record = new S3EventNotification.S3EventNotificationRecord(
                "us-east-1", null, null, null,
                null, null, null,
                new S3EventNotification.S3Entity(
                        null,
                        new S3EventNotification.S3BucketEntity("file-upload-rs-app", null, null),
                        new S3EventNotification.S3ObjectEntity("uploaded/test.csv", (Integer) null, null, null),
                        null
                ),
                null
        );

        S3Event s3Event = new S3Event(Collections.singletonList(record));
        return s3Event;
    }
}

package com.myorg.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.myorg.responses.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.MalformedURLException;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GetFileNameHandlerTest {

    @Mock
    private S3Presigner presigner;

    @Mock
    private Context context;

    private GetFileNameHandler handler;

    @Test
    public void testHandleRequest() throws MalformedURLException {
        Map<String, Object> input = new HashMap<>();
        Map<String, String> queryStringParameters = new HashMap<>();
        queryStringParameters.put("name", "test2.csv");
        input.put("queryStringParameters", queryStringParameters);

        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(URI.create("https://example.com").toURL());

        when(presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedRequest);

        ApiResponse response = handler.handleRequest(input, context);

        verify(presigner, times(1)).presignGetObject(any(GetObjectPresignRequest.class));
        assert response.getStatusCode() == 200;
    }
}

package com.myorg.lambdas;

import com.myorg.dto.ProductDto;
import com.myorg.services.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.amazonaws.services.lambda.runtime.events.SQSEvent.SQSMessage;
import com.google.gson.Gson;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CatalogBatchProcessHandlerTest {

    @Mock
    private ProductService mockProductService;

    @Mock
    private Gson mockGson;

    @Mock
    private Context mockContext;

    @InjectMocks
    private CatalogBatchProcessHandler handler;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testHandleRequest() {
        SQSEvent event = new SQSEvent();
        SQSMessage message = new SQSMessage();
        message.setBody("{\"id\": \"123\", \"title\": \"Test Product\", \"description\": \"Description\", \"price\": 20.0, \"count\": 20}");
        event.setRecords(Collections.singletonList(message));

        when(mockGson.fromJson(anyString(), any(Class.class))).thenReturn(new ProductDto("123", "Test Product", "Description", 20.0, 20));
        doNothing().when(mockProductService).saveProduct(any(ProductDto.class));

        String result = handler.handleRequest(event, mockContext);

        assertEquals("Success", result);
    }
}

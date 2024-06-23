//package com.myorg.lambdas;
//
//import com.amazonaws.services.lambda.runtime.Context;
//import com.google.gson.Gson;
//import com.myorg.entities.Product;
//import com.myorg.exceptions.ProductNotFoundException;
//import com.myorg.responses.ApiGatewayResponse;
//import com.myorg.services.ProductService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class GetProductByIdHandlerTest {
//
//    @Mock
//    private ProductService productService;
//
//    @Mock
//    private Context context;
//
//    @InjectMocks
//    private GetProductByIdHandler handler;
//
//    private final Gson gson = new Gson();
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    public void testHandleRequest_ValidProductId_Returns200() throws ProductNotFoundException {
//        String productId = "1";
//        Product mockProduct = new Product(productId, "Product A", "Good product", 10.00);
//
//        Map<String, Object> input = new HashMap<>();
//        Map<String, String> pathParameters = Collections.singletonMap("productId", String.valueOf(productId));
//        input.put("pathParameters", pathParameters);
//
//        when(productService.getProductById(productId)).thenReturn(mockProduct);
//
//        ApiGatewayResponse response = handler.handleRequest(input, context);
//
//        assertEquals(200, response.getStatusCode());
//        assertEquals("application/json", response.getHeaders().get("Content-Type"));
//
//        Product returnedProduct = gson.fromJson(response.getBody(), Product.class);
//        assertNotNull(returnedProduct);
//        assertEquals(productId, returnedProduct.getId());
//        assertEquals(mockProduct.getTitle(), returnedProduct.getTitle());
//        assertEquals(mockProduct.getDescription(), returnedProduct.getDescription());
//        assertEquals(mockProduct.getPrice(), returnedProduct.getPrice());
//    }
//
//    @Test
//    public void testHandleRequest_MissingProductId_Returns400() {
//        Map<String, Object> input = new HashMap<>();
//
//        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> handler.handleRequest(input, context));
//
//        assertNotNull(exception);
//        assertEquals("ProductId not found in path parameters", exception.getMessage());
//    }
//
//    @Test
//    public void testHandleRequest_ProductNotFound_Returns404() throws ProductNotFoundException {
//        String productId = "456";
//
//        Map<String, Object> input = new HashMap<>();
//        Map<String, String> pathParameters = Collections.singletonMap("productId", String.valueOf(productId));
//        input.put("pathParameters", pathParameters);
//
//        when(productService.getProductById(anyString())).thenThrow(new ProductNotFoundException("Product not found"));
//
//        ApiGatewayResponse response = handler.handleRequest(input, context);
//
//        assertEquals(404, response.getStatusCode());
//        assertEquals("application/json", response.getHeaders().get("Content-Type"));
//        assertEquals(gson.toJson("message: Product Not Found"), response.getBody());
//    }
//}

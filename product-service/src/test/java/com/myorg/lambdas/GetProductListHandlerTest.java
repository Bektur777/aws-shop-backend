//package com.myorg.lambdas;
//
//import com.amazonaws.services.lambda.runtime.Context;
//import com.google.gson.Gson;
//import com.google.gson.reflect.TypeToken;
//import com.myorg.entities.Product;
//import com.myorg.responses.ApiGatewayResponse;
//import com.myorg.services.ProductService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class GetProductListHandlerTest {
//
//    @Mock
//    private ProductService productService;
//
//    @Mock
//    private Context context;
//
//    @InjectMocks
//    private GetProductListHandler handler;
//
//    private List<Product> productList;
//
//    private final Gson gson = new Gson();
//
//    @BeforeEach
//    public void setup() {
//        MockitoAnnotations.openMocks(this);
//
//        productList = new ArrayList<>();
//        Product productA = new Product("1", "Product A", "Good product", 10.0);
//        Product productB = new Product("2", "Product B", "Good product", 20.0);
//        Product productC = new Product("3", "Product C", "Good product", 30.0);
//        productList.add(productA);
//        productList.add(productB);
//        productList.add(productC);
//    }
//
//    @Test
//    public void testHandleRequest_Returns200WithProducts() {
//        when(productService.getAllProducts()).thenReturn(productList);
//
//        ApiGatewayResponse response = handler.handleRequest(null, context);
//
//        assertEquals(200, response.getStatusCode());
//        assertEquals("application/json", response.getHeaders().get("Content-Type"));
//
//        String responseBody = response.getBody();
//        List<Product> returnedProducts = gson.fromJson(responseBody, new TypeToken<List<Product>>(){}.getType());
//        assertEquals(productList.size(), returnedProducts.size());
//
//        for (int i = 0; i < productList.size(); i++) {
//            Product expected = productList.get(i);
//            Product actual = returnedProducts.get(i);
//            assertEquals(expected.getId(), actual.getId());
//            assertEquals(expected.getTitle(), actual.getTitle());
//            assertEquals(expected.getDescription(), actual.getDescription());
//            assertEquals(expected.getPrice(), actual.getPrice());
//        }
//    }
//}

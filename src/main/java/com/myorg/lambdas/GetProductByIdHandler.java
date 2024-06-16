package com.myorg.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.myorg.entities.Product;
import com.myorg.exceptions.ProductNotFoundException;
import com.myorg.responses.ApiGatewayResponse;
import com.myorg.services.ProductService;

import java.util.Collections;
import java.util.Map;

public class GetProductByIdHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final ProductService productService = new ProductService();
    private final Gson gson = new Gson();

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {

        Map<String, String> pathParameters = (Map<String, String>) input.get("pathParameters");
        if (pathParameters == null || !pathParameters.containsKey("productId")) {
            throw new IllegalArgumentException("ProductId not found in path parameters");
        }

        if (pathParameters.get("productId") == null) {
            throw new ProductNotFoundException("Product Not Found");
        }

        long productId = Long.parseLong(pathParameters.get("productId"));

        try {
            Product productById = productService.getProductById(productId);
            return ApiGatewayResponse.builder()
                    .statusCode(200)
                    .headers(Collections.singletonMap("Content-Type", "application/json"))
                    .body(gson.toJson(productById))
                    .build();
        } catch (ProductNotFoundException e) {
            return ApiGatewayResponse.builder()
                    .statusCode(404)
                    .headers(Collections.singletonMap("Content-Type", "application/json"))
                    .body(gson.toJson("message: Product Not Found"))
                    .build();
        }
    }
}

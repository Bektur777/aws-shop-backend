package com.myorg.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.myorg.entities.Product;
import com.myorg.responses.ApiGatewayResponse;
import com.myorg.services.ProductService;
import java.util.Map;

import java.util.Collections;
import java.util.List;
import java.util.HashMap;

public class GetProductListHandler implements RequestHandler<Object, ApiGatewayResponse> {

    private  final ProductService productService = new ProductService();

    @Override
    public ApiGatewayResponse handleRequest(Object o, Context context) {
        Gson gson = new Gson();
        List<Product> allProducts = productService.getAllProducts();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET,POST,OPTIONS");

        return ApiGatewayResponse.builder()
                .statusCode(200)
                .headers(headers)
                .body(gson.toJson(allProducts))
                .build();
    }
}

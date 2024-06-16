package com.myorg.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.myorg.entities.Product;
import com.myorg.responses.ApiGatewayResponse;
import com.myorg.services.ProductService;

import java.util.Collections;
import java.util.List;

public class GetProductListHandler implements RequestHandler<Object, ApiGatewayResponse> {

    private  final ProductService productService = new ProductService();

    @Override
    public ApiGatewayResponse handleRequest(Object o, Context context) {
        Gson gson = new Gson();
        List<Product> allProducts = productService.getAllProducts();

        return ApiGatewayResponse.builder()
                .statusCode(200)
                .headers(Collections.singletonMap("Content-Type", "application/json"))
                .body(gson.toJson(allProducts))
                .build();
    }
}

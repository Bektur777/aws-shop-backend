package com.myorg.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.myorg.dto.ProductDto;
import com.myorg.responses.ApiGatewayResponse;
import com.myorg.services.ProductService;
import java.util.Map;

import java.util.List;
import java.util.HashMap;

public class GetProductListHandler implements RequestHandler<Object, ApiGatewayResponse> {

    private  final ProductService productService = new ProductService();
    private final Gson gson = new Gson();

    @Override
    public ApiGatewayResponse handleRequest(Object o, Context context) {
        LambdaLogger logger = context.getLogger();

        logger.log("Request id: " + context.getAwsRequestId());
        logger.log("Function name: " + context.getFunctionName());

        try {
            List<ProductDto> allProducts = productService.getAllProducts();

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Access-Control-Allow-Origin", "*");
            headers.put("Access-Control-Allow-Methods", "GET");

            return ApiGatewayResponse.builder()
                    .statusCode(200)
                    .headers(headers)
                    .body(gson.toJson(allProducts))
                    .build();
        } catch (Exception e) {
            logger.log("ERROR: " + e.getMessage());
            return ApiGatewayResponse.builder()
                    .statusCode(500)
                    .body("Error get all products: " + e.getMessage())
                    .build();
        }
    }
}

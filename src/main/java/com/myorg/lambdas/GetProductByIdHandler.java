package com.myorg.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.myorg.dto.ProductDto;
import com.myorg.entities.Product;
import com.myorg.exceptions.ProductNotFoundException;
import com.myorg.responses.ApiGatewayResponse;
import com.myorg.services.ProductService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GetProductByIdHandler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

    private final ProductService productService = new ProductService();
    private final Gson gson = new Gson();

    private Map<String, String> pathParameters;

    @Override
    public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
        pathParameters = (Map<String, String>) input.get("pathParameters");

        LambdaLogger logger = context.getLogger();

        logger.log("Request id: " + context.getAwsRequestId());
        logger.log("Function name: " + context.getFunctionName());
        logger.log("Path parameters: " + pathParameters);

        if (pathParameters == null || !pathParameters.containsKey("productId")) {
            logger.log("ERROR: Invalid path parameters");
            throw new IllegalArgumentException("ProductId not found in path parameters");
        }

        String productId = String.valueOf(pathParameters.get("productId"));

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        headers.put("Access-Control-Allow-Methods", "GET");

        try {
            Product productById = productService.getProductById(productId);
            logger.log("SUCCESS: " + productId);
            return apiResponse(200, headers, gson.toJson(productById));
        } catch (ProductNotFoundException e) {
            logger.log("WARN: " + e.getMessage());
            return apiResponse(404, headers, gson.toJson("{message: " + e.getMessage() + '}'));
        } catch (Exception e) {
            logger.log("ERROR: " + e.getMessage());
            return ApiGatewayResponse.builder()
                    .statusCode(500)
                    .body("Error get all products: " + e.getMessage())
                    .build();
        }
    }

    private ApiGatewayResponse apiResponse(int codeStatus, Map<String, String> headers, String message) {
        return ApiGatewayResponse.builder()
                .statusCode(codeStatus)
                .headers(headers)
                .body(message)
                .build();
    }
}

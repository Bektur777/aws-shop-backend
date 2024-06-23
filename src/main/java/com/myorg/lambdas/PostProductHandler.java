package com.myorg.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.myorg.dto.ProductDto;
import com.myorg.responses.ApiGatewayResponse;
import com.myorg.services.ProductService;

import java.util.HashMap;
import java.util.Map;

public class PostProductHandler implements RequestHandler<ProductDto, ApiGatewayResponse> {

    private final ProductService productService = new ProductService();
    private final Gson gson = new Gson();

    @Override
    public ApiGatewayResponse handleRequest(ProductDto productDto, Context context) {
        LambdaLogger logger = context.getLogger();

        logger.log("Request id: " + context.getAwsRequestId());
        logger.log("Function name: " + context.getFunctionName());

        /**
         * Validate ProductDto
         */
        if (!isValidProductDto(productDto)) {
            logger.log("Validation failed for productDto: " + productDto);
            return ApiGatewayResponse.builder()
                    .statusCode(400)
                    .body("Bad Request: Invalid product data")
                    .build();
        }

        try {

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("Access-Control-Allow-Origin", "*");
            headers.put("Access-Control-Allow-Methods", "PUT");

            productService.saveProduct(productDto);

            return ApiGatewayResponse.builder()
                    .statusCode(200)
                    .headers(headers)
                    .body(gson.toJson("{message: Ok}"))
                    .build();
        }
        catch (Exception e) {
            logger.log("ERROR: " + e.getMessage());
            return ApiGatewayResponse.builder()
                    .statusCode(500)
                    .body("Error creating product: " + e.getMessage())
                    .build();
        }
    }

    private boolean isValidProductDto(ProductDto productDto) {
        return productDto != null
                && productDto.getTitle() != null && !productDto.getTitle().trim().isEmpty()
                && productDto.getPrice() != null && productDto.getPrice() > 0
                && productDto.getCount() != null && productDto.getCount() > 0;
    }
}

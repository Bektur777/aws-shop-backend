package com.myorg.config;

import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.lambda.Function;
import software.constructs.Construct;

import java.util.Collections;

public class ApiGatewayConfig {

    public static RestApi createGetRequestsApi(Construct scope, String id,
                                               Function getProductListLambda,
                                               Function getProductByIdLambda) {
        RestApi api = RestApi.Builder.create(scope, id)
                .defaultCorsPreflightOptions(CorsOptions.builder()
                        .allowOrigins(Cors.ALL_ORIGINS)
                        .allowMethods(Cors.ALL_METHODS)
                        .allowHeaders(Collections.singletonList("*"))
                        .build())
                .description("API GET запросов для получения информации о продуктах")
                .build();

        Resource products = api.getRoot().addResource("products");

        LambdaIntegration getProductslambdaIntegration = LambdaIntegration.Builder.create(getProductListLambda).build();
        products.addMethod("GET", getProductslambdaIntegration);

        Resource product = products.addResource("{productId}");

        LambdaIntegration getProductByIdLambdaIntegration = LambdaIntegration.Builder.create(getProductByIdLambda).build();
        product.addMethod("GET", getProductByIdLambdaIntegration);

        return api;
    }
}

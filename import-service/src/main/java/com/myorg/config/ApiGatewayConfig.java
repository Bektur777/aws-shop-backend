package com.myorg.config;

import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.lambda.Function;
import software.constructs.Construct;

import java.util.Collections;

import static software.amazon.awscdk.services.apigateway.Cors.ALL_METHODS;
import static software.amazon.awscdk.services.apigateway.Cors.ALL_ORIGINS;

public class ApiGatewayConfig {

    public static RestApi createApiRequest(Construct scope, String id,
                                        Function importProductsFile) {
        RestApi restApi = RestApi.Builder.create(scope, id)
                .defaultCorsPreflightOptions(CorsOptions.builder()
                        .allowMethods(ALL_METHODS)
                        .allowOrigins(ALL_ORIGINS)
                        .allowHeaders(Collections.singletonList("*"))
                        .build()
                )
                .cloudWatchRole(true)
                .description("API запросов для работы с imports file service")
                .build();

        Resource importResource = restApi.getRoot().addResource("import");

        LambdaIntegration lambdaIntegration = LambdaIntegration.Builder.create(importProductsFile)
                .build();
        importResource.addMethod("GET", lambdaIntegration);
        return restApi;
    }
}

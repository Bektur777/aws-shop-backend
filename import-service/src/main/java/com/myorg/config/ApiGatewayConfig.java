package com.myorg.config;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.services.apigateway.*;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.CfnPermission;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.IFunction;
import software.constructs.Construct;

import java.util.Collections;
import java.util.List;

import static software.amazon.awscdk.services.apigateway.Cors.ALL_METHODS;
import static software.amazon.awscdk.services.apigateway.Cors.ALL_ORIGINS;

public class ApiGatewayConfig {

    public static RestApi createApiRequest(Construct scope, String id,
                                        Function importProductsFile) {
        IFunction basicAuthorizerLambda = Function.fromFunctionName(scope, "AuthFunction",
                "AuthorizationServiceStack-BasicAuthorizationLambda-ymNP1ZLIK1sg");

        RestApi restApi = RestApi.Builder.create(scope, id)
                .defaultCorsPreflightOptions(CorsOptions.builder()
                        .allowMethods(ALL_METHODS)
                        .allowOrigins(ALL_ORIGINS)
                        .allowHeaders(Collections.singletonList("*"))
                        .build()
                )
                .description("API запросов для работы с imports file service")
                .build();

        Resource importResource = restApi.getRoot().addResource("import");

        LambdaIntegration lambdaIntegration = LambdaIntegration.Builder.create(importProductsFile)
                .build();

        RequestAuthorizer authorizer = RequestAuthorizer.Builder.create(scope, "BasicAuthorizer")
                .handler(basicAuthorizerLambda)
                .identitySources(Collections.singletonList(IdentitySource.header("Authorization")))
                .build();

        importResource.addMethod("GET", lambdaIntegration, MethodOptions.builder()
                .authorizationType(AuthorizationType.CUSTOM)
                .authorizer(authorizer)
                .build());

        return restApi;
    }
}

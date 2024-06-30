package com.myorg.config;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.Collections;
import java.util.List;

public class LambdaConfig {

    private static final String PRODUCT_TABLE_NAME = "arn:aws:dynamodb:eu-central-1:975050038015:table/products";
    private static final String STOCK_TABLE_NAME = "arn:aws:dynamodb:eu-central-1:975050038015:table/stocks";
    private static final String PUT_ITEM = "dynamodb:PutItem";
    private static final String GET_ITEM = "dynamodb:GetItem";
    private static final String SCAN = "dynamodb:Scan";

    public static Function createPostProductLambda(Construct scope, String id) {
        return createLambdaToManipulatingProducts(scope, id,
                "com.myorg.lambdas.PostProductHandler::handleRequest",
                List.of(PRODUCT_TABLE_NAME, STOCK_TABLE_NAME), List.of(PUT_ITEM));
    }

    public static Function createGetProductListLambda(Construct scope, String id) {
        return createLambdaToManipulatingProducts(scope, id,
                "com.myorg.lambdas.GetProductListHandler::handleRequest",
                List.of(PRODUCT_TABLE_NAME, STOCK_TABLE_NAME), List.of(SCAN));
    }

    public static Function createGetProductByIdLambda(Construct construct, String id) {
        return createLambdaToManipulatingProducts(construct, id,
                "com.myorg.lambdas.GetProductByIdHandler::handleRequest",
                List.of(PRODUCT_TABLE_NAME, STOCK_TABLE_NAME), List.of(GET_ITEM));
    }

    private static Function createLambdaToManipulatingProducts(Construct construct, String id,
                                                               String handler, List<String> resources, List<String> actions) {
        Function function = Function.Builder.create(construct, id)
                .runtime(Runtime.JAVA_17)
                .architecture(Architecture.ARM_64)
                .handler(handler)
                .code(Code.fromAsset("target/aws-shop-backend-0.1.jar"))
                .timeout(Duration.seconds(20))
                .build();

        function.addToRolePolicy(PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .actions(actions)
                .resources(resources)
                .build()
        );

        return function;
    }
}

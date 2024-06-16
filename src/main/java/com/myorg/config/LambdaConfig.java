package com.myorg.config;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

public class LambdaConfig {

    public static Function createGetProductListLambda(Construct scope, String id) {
        return Function.Builder.create(scope, id)
                .runtime(Runtime.JAVA_17)
                .architecture(Architecture.ARM_64)
                .handler("com.myorg.lambdas.GetProductListHandler::handleRequest")
                .code(Code.fromAsset("target/aws-shop-backend-0.1.jar"))
                .timeout(Duration.seconds(20))
                .build();
    }

    public static Function createGetProductByIdLambda(Construct construct, String id) {
        return Function.Builder.create(construct, id)
                .runtime(Runtime.JAVA_17)
                .architecture(Architecture.ARM_64)
                .handler("com.myorg.lambdas.GetProductByIdHandler::handleRequest")
                .code(Code.fromAsset("target/aws-shop-backend-0.1.jar"))
                .timeout(Duration.seconds(20))
                .build();
    }
}

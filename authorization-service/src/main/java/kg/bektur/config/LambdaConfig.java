package kg.bektur.config;

import io.github.cdimascio.dotenv.Dotenv;
import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.constructs.Construct;

import java.util.List;
import java.util.Map;

public class LambdaConfig {

    public static Function basicAuthorizerLambda(Construct scope, String id) {
        return createLambdaFunctionToManipulatingAuthorization(scope, id,
                "kg.bektur.handler.BasicAuthorizationHandler::handleRequest");
    }

    public static Function createLambdaFunctionToManipulatingAuthorization(Construct scope, String id,
                                                                           String handler) {
        Function function = Function.Builder.create(scope, id)
                .runtime(Runtime.JAVA_17)
                .architecture(Architecture.ARM_64)
                .handler(handler)
                .code(Code.fromAsset("target/authorization-service-0.1.jar"))
                .environment(Map.of("bektur777", "TEST_PASSWORD"))
                .timeout(Duration.seconds(20))
                .memorySize(256)
                .build();

        function.addToRolePolicy(PolicyStatement.Builder.create()
                .effect(Effect.ALLOW)
                .actions(List.of("lambda:InvokeFunction"))
                .resources(List.of("arn:aws:lambda:eu-central-1:975050038015:function:AuthorizationServiceStack-BasicAuthorizationLambda-ymNP1ZLIK1sg"))
                .build()
        );

        return function;
    }
}

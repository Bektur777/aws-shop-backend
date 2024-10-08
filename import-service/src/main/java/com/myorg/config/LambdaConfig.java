package com.myorg.config;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.lambda.Architecture;
import software.amazon.awscdk.services.lambda.Code;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Runtime;
import software.amazon.awscdk.services.lambda.eventsources.S3EventSource;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.EventType;
import software.amazon.awscdk.services.s3.NotificationKeyFilter;
import software.constructs.Construct;

import java.util.Collections;
import java.util.List;

public class LambdaConfig {

    private static final String S3_RESOURCE_NAME = "arn:aws:s3:::file-upload-rs-app/*";
    private static final String SQS_RESOURCE_NAME = "arn:aws:sqs:eu-central-1:975050038015:catalogItemsQueue";
    private static final String S3_GET_OBJECT = "s3:GetObject";
    private static final String S3_PUT_OBJECT = "s3:PutObject";
    private static final String SQS_SEND_MESSAGE = "sqs:SendMessage";

    public static Function importProductsFile(Construct scope, String id) {
            return createLambdaToManipulatingImports(scope, id,
                    "com.myorg.lambdas.GetFileNameHandler::handleRequest",
                    List.of(S3_GET_OBJECT, S3_PUT_OBJECT), List.of(S3_RESOURCE_NAME));
    }

    public static Function importFileParser(Construct scope, String id, Bucket bucket) {
        Function function = createLambdaToManipulatingImports(scope, id,
                "com.myorg.lambdas.ParseProductsHandler::handleRequest",
                List.of(S3_GET_OBJECT, S3_PUT_OBJECT, SQS_SEND_MESSAGE),
                List.of(S3_RESOURCE_NAME, SQS_RESOURCE_NAME));

        S3EventSource s3EventSource = S3EventSource.Builder.create(bucket)
                .events(Collections.singletonList(EventType.OBJECT_CREATED))
                .filters(Collections.singletonList(NotificationKeyFilter.builder().prefix("uploaded/").build()))
                .build();

        function.addEventSource(s3EventSource);

        return function;
    }

    private static Function createLambdaToManipulatingImports(Construct scope, String id,
                                                       String handler,
                                                       List<String> actions,
                                                       List<String> resources) {
        Function function = Function.Builder.create(scope, id)
                .runtime(Runtime.JAVA_17)
                .architecture(Architecture.ARM_64)
                .handler(handler)
                .code(Code.fromAsset("target/import-service-0.1.jar"))
                .timeout(Duration.seconds(40))
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

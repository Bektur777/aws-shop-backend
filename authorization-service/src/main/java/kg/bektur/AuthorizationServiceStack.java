package kg.bektur;

import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.lambda.Permission;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import static kg.bektur.config.LambdaConfig.basicAuthorizerLambda;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class AuthorizationServiceStack extends Stack {
    public AuthorizationServiceStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AuthorizationServiceStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here

        // example resource
        // final Queue queue = Queue.Builder.create(this, "AuthorizationServiceQueue")
        //         .visibilityTimeout(Duration.seconds(300))
        //         .build();

        Function basicAuthorizerLambda = basicAuthorizerLambda(this, "BasicAuthorizationLambda");
    }
}

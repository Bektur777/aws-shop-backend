package com.myorg;

import com.myorg.config.ApiGatewayConfig;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.lambda.Function;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import static com.myorg.config.LambdaConfig.createGetProductByIdLambda;
import static com.myorg.config.LambdaConfig.createGetProductListLambda;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class AwsShopBackendStack extends Stack {
    public AwsShopBackendStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsShopBackendStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Function getProductListLambda = createGetProductListLambda(this, "GetProductListLambda");
        Function getProductByIdLambda = createGetProductByIdLambda(this, "GetProductByIdLambda");

        RestApi api = ApiGatewayConfig.createGetRequestsApi(this, "ProductGetRequestsServiceApi",
                getProductListLambda, getProductByIdLambda);
    }
}

package com.myorg;

import com.myorg.config.ApiGatewayConfig;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.sns.NumericConditions;
import software.amazon.awscdk.services.sns.StringConditions;
import software.amazon.awscdk.services.sns.SubscriptionFilter;
import software.amazon.awscdk.services.sns.Topic;
import software.amazon.awscdk.services.sns.subscriptions.EmailSubscription;
import software.amazon.awscdk.services.sqs.Queue;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.myorg.config.LambdaConfig.*;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class AwsShopBackendStack extends Stack {
    public AwsShopBackendStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public AwsShopBackendStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        Topic createProductTopic = Topic.Builder.create(this, "CreateProductTopic")
                .topicName("createProductTopic")
                .build();

        Map<String, SubscriptionFilter> filters = new HashMap<>();
        filters.put("price", SubscriptionFilter.numericFilter(
                NumericConditions.builder()
                        .greaterThan(500)
                        .build()
        ));

        EmailSubscription allEmailSubscription = EmailSubscription.Builder.create("ubei033@gmail.com")
                .filterPolicy(filters)
                .build();

        createProductTopic.addSubscription(allEmailSubscription);

        Queue catalogItemsQueue = Queue.Builder.create(this, "CatalogItemsQueue")
                .queueName("catalogItemsQueue")
                .build();

        Function getProductListLambda = createGetProductListLambda(this, "GetProductListLambda");
        Function getProductByIdLambda = createGetProductByIdLambda(this, "GetProductByIdLambda");
        Function createProductLambda = createPostProductLambda(this, "CreateProductLambda");
        Function catalogBatchProcessLambda = createCatalogBatchProcessLambda(this, "CatalogBatchProcessLambda",
                catalogItemsQueue, createProductTopic);

        RestApi getApi = ApiGatewayConfig.createApiRequest(this, "ProductsRequestsServiceApi",
                getProductListLambda, getProductByIdLambda, createProductLambda);
    }
}

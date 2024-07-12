package com.myorg.lambdas;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.google.gson.Gson;
import com.myorg.dto.ProductDto;
import com.myorg.services.ProductService;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.MessageAttributeValue;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

import java.util.Map;

public class CatalogBatchProcessHandler implements RequestHandler<SQSEvent, String> {

    private final ProductService productService;
    private final Gson gson;
    private final SnsClient snsClient;
    private final String topicArn;

    public CatalogBatchProcessHandler() {
        productService = new ProductService();
        gson = new Gson();
        snsClient = SnsClient.create();
        topicArn = "arn:aws:sns:eu-central-1:975050038015:createProductTopic";
    }

    @Override
    public String handleRequest(SQSEvent event, Context context) {
        LambdaLogger logger = context.getLogger();

        logger.log("Request id: " + context.getAwsRequestId());
        logger.log("Function name: " + context.getFunctionName());

        event.getRecords().forEach(message -> {
            logger.log("Processing message: " + message.getBody());
            ProductDto productDto = gson.fromJson(message.getBody(), ProductDto.class);

            try {
                productService.saveProduct(productDto);
                logger.log("Successfully processed message for product: " + productDto.getId());

                String notificationMessage = "Product created: " + productDto.getId() + " " +
                        productDto.getTitle() + " " + productDto.getDescription() +
                        " " + productDto.getPrice() + " " + productDto.getCount();

                PublishRequest publishRequest = PublishRequest.builder()
                        .topicArn(topicArn)
                        .message(notificationMessage)
                        .messageAttributes(Map.of(
                            "price", MessageAttributeValue.builder()
                                    .dataType("Number")
                                    .stringValue(String.valueOf(productDto.getPrice()))
                                    .build()
                            )
                        )
                        .build();
                PublishResponse publishResponse = snsClient.publish(publishRequest);
                logger.log("SNS Publish response: " + publishResponse.messageId());

            } catch (Exception e) {
                logger.log("Error processing message: " + e.getMessage());
            }
        });

        return "Success";
    }
}

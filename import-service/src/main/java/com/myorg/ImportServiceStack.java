package com.myorg;

import com.myorg.config.ApiGatewayConfig;
import software.amazon.awscdk.services.apigateway.RestApi;
import software.amazon.awscdk.services.lambda.Function;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;

import static com.myorg.config.LambdaConfig.importFileParser;
import static com.myorg.config.LambdaConfig.importProductsFile;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class ImportServiceStack extends Stack {
    public ImportServiceStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ImportServiceStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

        // The code that defines your stack goes here

        // example resource
        // final Queue queue = Queue.Builder.create(this, "ImportServiceQueue")
        //         .visibilityTimeout(Duration.seconds(300))
        //         .build();

        S3Client s3Client = S3Client.builder()
                .region(Region.EU_CENTRAL_1)
                .build();

        Bucket bucket = Bucket.Builder.create(this, "ImportParse")
                .bucketName("file-upload-rs-app")
                .build();

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket("file-upload-rs-app")
                .key("uploaded/test5.csv")
                .build();

        String content = """
                id,title,description,price,count
                1,Title,description,5,10
                2,Title,description,5,10
                """;
        s3Client.putObject(request, RequestBody.fromString(content));


        Function importProductsFile = importProductsFile(this, "ImportProductsFile", bucket);
        bucket.grantReadWrite(importProductsFile);

        Function importFileParser = importFileParser(this, "importFileParser", bucket);
        bucket.grantReadWrite(importProductsFile);

        RestApi restApi = ApiGatewayConfig.createApiRequest(this, "ImportProductsRequestsServiceApi",
                importProductsFile);
    }
}

package kg.bektur.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayCustomAuthorizerEvent;
import com.amazonaws.services.lambda.runtime.events.IamPolicyResponse;

import java.nio.charset.StandardCharsets;
import java.util.*;

public class BasicAuthorizationHandler implements RequestHandler<APIGatewayCustomAuthorizerEvent, IamPolicyResponse> {

    @Override
    public IamPolicyResponse handleRequest(APIGatewayCustomAuthorizerEvent apiGatewayCustomAuthorizerEvent, Context context) {
        Map<String, String> headers = apiGatewayCustomAuthorizerEvent.getHeaders();
        LambdaLogger logger = context.getLogger();

        logger.log("Request Headers: " + headers.toString());

        if (!headers.containsKey("Authorization")) {
            logger.log("ERROR: Authorization header not present");
            return generatePolicyResponse(apiGatewayCustomAuthorizerEvent, "Deny", "Authorization header missing");
        }

        String authHeader = headers.get("Authorization");
        logger.log("Authorization header: " + authHeader);

        if (!authHeader.startsWith("Basic ")) {
            logger.log("ERROR: Invalid Authorization header");
            return generatePolicyResponse(apiGatewayCustomAuthorizerEvent, "Deny", "Invalid Authorization header");
        }

        String base64Credentials = authHeader.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);
        String[] values = credentials.split(":", 2);

        logger.log("INFO: " + credentials);

        if (values.length != 2) {
            logger.log("ERROR: Invalid authorization token " + Arrays.toString(values));
            return generatePolicyResponse(apiGatewayCustomAuthorizerEvent, "Deny", "Invalid authorization token");
        }

        String username = values[0];
        String password = values[1];

        String expectedPassword = System.getenv(username);

        if (expectedPassword != null && expectedPassword.equals(password)) {
            IamPolicyResponse allow = generatePolicyResponse(apiGatewayCustomAuthorizerEvent, "Allow", username);
            logger.log("INFO: " + allow);
            return allow;
        } else {
            logger.log("ERROR: Access denied");
            return generatePolicyResponse(apiGatewayCustomAuthorizerEvent, "Deny", "Access denied");
        }
    }

    private IamPolicyResponse generatePolicyResponse(APIGatewayCustomAuthorizerEvent apiGatewayCustomAuthorizerEvent, String effect, String principalId) {
        String methodArn = apiGatewayCustomAuthorizerEvent.getMethodArn();

        return IamPolicyResponse.builder()
                .withPrincipalId(principalId)
                .withPolicyDocument(IamPolicyResponse.PolicyDocument.builder()
                        .withVersion(IamPolicyResponse.VERSION_2012_10_17)
                        .withStatement(List.of(IamPolicyResponse.Statement.builder()
                                .withEffect(effect)
                                .withAction(IamPolicyResponse.EXECUTE_API_INVOKE)
                                .withResource(List.of(methodArn))
                                .build()))
                        .build())
                .build();
    }
}

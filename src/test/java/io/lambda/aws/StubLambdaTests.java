package io.lambda.aws;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lambda.aws.config.ObjectMapperConfig;
import io.lambda.aws.model.AwsRequestEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * @author GoodforGod
 * @since 27.10.2020
 */
class StubLambdaTests extends Assertions {

    @Test
    void handled() throws Exception {
        ObjectMapper mapper = new ObjectMapperConfig().getMapper();
        String s = "{\"httpMethod\": \"POST\", \"body\": \"{\\r\\n  \\\"url\\\": \\\"https://www.vamsvet.ru/catalog/product/lampa-metalogalogennaya-g12-150w-4200k-prozrachnaya-03806/\\\"\\r\\n}\", \"resource\": \"/\", \"requestContext\": {\"resourceId\": \"123456\", \"apiId\": \"1234567890\", \"resourcePath\": \"/\", \"httpMethod\": \"POST\", \"requestId\": \"c6af9ac6-7b61-11e6-9a41-93e8deadbeef\", \"accountId\": \"123456789012\", \"stage\": \"Prod\", \"identity\": {\"apiKey\": null, \"userArn\": null, \"cognitoAuthenticationType\": null, \"caller\": null, \"userAgent\": \"Custom User Agent String\", \"user\": null, \"cognitoIdentityPoolId\": null, \"cognitoAuthenticationProvider\": null, \"sourceIp\": \"127.0.0.1\", \"accountId\": null}, \"extendedRequestId\": null, \"path\": \"/\", \"protocol\": \"HTTP/1.1\", \"domainName\": \"localhost:3000\", \"requestTimeEpoch\": 1603917963, \"requestTime\": \"28/Oct/2020:20:46:03 +0000\"}, \"queryStringParameters\": null, \"multiValueQueryStringParameters\": null, \"headers\": {\"Content-Type\": \"application/json\", \"User-Agent\": \"PostmanRuntime/7.26.8\", \"Accept\": \"*/*\", \"Postman-Token\": \"ef6b1eb1-e072-4a69-a777-d1bb8d036414\", \"Host\": \"localhost:3000\", \"Accept-Encoding\": \"gzip, deflate, br\", \"Connection\": \"keep-alive\", \"Content-Length\": \"114\", \"X-Forwarded-Proto\": \"http\", \"X-Forwarded-Port\": \"3000\"}, \"multiValueHeaders\": {\"Content-Type\": [\"application/json\"], \"User-Agent\": [\"PostmanRuntime/7.26.8\"], \"Accept\": [\"*/*\"], \"Postman-Token\": [\"ef6b1eb1-e072-4a69-a777-d1bb8d036414\"], \"Host\": [\"localhost:3000\"], \"Accept-Encoding\": [\"gzip, deflate, br\"], \"Connection\": [\"keep-alive\"], \"Content-Length\": [\"114\"], \"X-Forwarded-Proto\": [\"http\"], \"X-Forwarded-Port\": [\"3000\"]}, \"pathParameters\": null, \"stageVariables\": null, \"path\": \"/\", \"isBase64Encoded\": false}";
        AwsRequestEvent event = mapper.readValue(s, AwsRequestEvent.class);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(new URI("http://localhost:8080")).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertNotNull(response.body());
    }
}

package io.aws.lambda.simple.runtime.example;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 21.3.2021
 */
public class Response {

    private final String id;
    private final String message;

    public Response(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "Response{" + "id='" + id + '\'' + ", message='" + message + '\'' + '}';
    }
}

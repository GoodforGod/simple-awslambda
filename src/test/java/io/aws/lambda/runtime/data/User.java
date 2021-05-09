package io.aws.lambda.runtime.data;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 6.12.2020
 */
public class User {

    private String name;

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }
}

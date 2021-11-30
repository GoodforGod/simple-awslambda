package io.goodforgod.aws.simplelambda.mock;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 21.3.2021
 */
public class Request {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Request{" + "name='" + name + '\'' + '}';
    }
}

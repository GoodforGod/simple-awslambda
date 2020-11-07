package io.lambda.aws.convert;

/**
 * @author Anton Kurako (GoodforGod)
 * @since 7.11.2020
 */
public interface Converter {

    <T> T convertToType(String json, Class<T> type);

    String convertToJson(Object o);
}

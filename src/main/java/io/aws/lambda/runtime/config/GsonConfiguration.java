package io.aws.lambda.runtime.config;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.FieldNamingStrategy;
import com.google.gson.LongSerializationPolicy;
import io.micronaut.context.annotation.ConfigurationProperties;

import java.text.SimpleDateFormat;

/**
 * @see io.aws.lambda.runtime.convert.impl.GsonConverter
 * @author Anton Kurako (GoodforGod)
 * @since 25.04.2021
 */
@ConfigurationProperties("gson")
public class GsonConfiguration {

    private String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"; // ISO 8601
    private FieldNamingPolicy fieldNamingPolicy = FieldNamingPolicy.IDENTITY;
    private LongSerializationPolicy longSerializationPolicy = LongSerializationPolicy.DEFAULT;
    private boolean serializeNulls = false;
    private boolean complexMapKeySerialization = false;
    private boolean generateNonExecutableJson = false;
    private boolean escapeHtmlChars = true;
    private boolean prettyPrinting = false;
    private boolean lenient = false;
    private boolean serializeSpecialFloatingPointValues = false;

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        final SimpleDateFormat format = new SimpleDateFormat(dateFormat);
        // check date format
        this.dateFormat = dateFormat;
    }

    public FieldNamingPolicy getFieldNamingPolicy() {
        return fieldNamingPolicy;
    }

    public void setFieldNamingPolicy(FieldNamingPolicy fieldNamingPolicy) {
        this.fieldNamingPolicy = fieldNamingPolicy;
    }

    public LongSerializationPolicy getLongSerializationPolicy() {
        return longSerializationPolicy;
    }

    public void setLongSerializationPolicy(LongSerializationPolicy longSerializationPolicy) {
        this.longSerializationPolicy = longSerializationPolicy;
    }

    public boolean isSerializeNulls() {
        return serializeNulls;
    }

    public void setSerializeNulls(boolean serializeNulls) {
        this.serializeNulls = serializeNulls;
    }

    public boolean isComplexMapKeySerialization() {
        return complexMapKeySerialization;
    }

    public void setComplexMapKeySerialization(boolean complexMapKeySerialization) {
        this.complexMapKeySerialization = complexMapKeySerialization;
    }

    public boolean isGenerateNonExecutableJson() {
        return generateNonExecutableJson;
    }

    public void setGenerateNonExecutableJson(boolean generateNonExecutableJson) {
        this.generateNonExecutableJson = generateNonExecutableJson;
    }

    public boolean isEscapeHtmlChars() {
        return escapeHtmlChars;
    }

    public void setEscapeHtmlChars(boolean escapeHtmlChars) {
        this.escapeHtmlChars = escapeHtmlChars;
    }

    public boolean isPrettyPrinting() {
        return prettyPrinting;
    }

    public void setPrettyPrinting(boolean prettyPrinting) {
        this.prettyPrinting = prettyPrinting;
    }

    public boolean isLenient() {
        return lenient;
    }

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public boolean isSerializeSpecialFloatingPointValues() {
        return serializeSpecialFloatingPointValues;
    }

    public void setSerializeSpecialFloatingPointValues(boolean serializeSpecialFloatingPointValues) {
        this.serializeSpecialFloatingPointValues = serializeSpecialFloatingPointValues;
    }
}

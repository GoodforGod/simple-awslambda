package io.goodforgod.aws.lambda.simple.testing;

import io.goodforgod.aws.lambda.simple.AbstractLambdaEntrypoint;
import io.goodforgod.aws.lambda.simple.convert.Converter;
import io.goodforgod.aws.lambda.simple.runtime.RuntimeContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;

/**
 * Class that is used for testing Lambdas
 *
 * @author Anton Kurako (GoodforGod)
 * @since 13.04.2022
 */
public final class AwsLambdaAssertions {

    public final class Input {

        private Input() {}

        private Function<RuntimeContext, byte[]> inputConverter;

        void validateInput() {
            if (inputConverter != null) {
                final String t = this.getClass().getName();
                throw new IllegalStateException(
                        t + " already had input and can't be reused, please create new instance of: " + t);
            }
        }

        @NotNull
        public AwsLambdaAssertions inputString(@NotNull String value) {
            validateInput();
            this.inputConverter = context -> value.getBytes();
            return AwsLambdaAssertions.this;
        }

        @NotNull
        public AwsLambdaAssertions inputStream(@NotNull InputStream value) {
            validateInput();
            this.inputConverter = context -> {
                try {
                    return value.readAllBytes();
                } catch (IOException e) {
                    throw new TestingAwsLambdaException(e);
                }
            };
            return AwsLambdaAssertions.this;
        }

        @NotNull
        public AwsLambdaAssertions inputBytes(byte[] value) {
            validateInput();
            this.inputConverter = context -> value;
            return AwsLambdaAssertions.this;
        }

        @NotNull
        public AwsLambdaAssertions inputJson(@NotNull Object value) {
            validateInput();
            this.inputConverter = context -> {
                final Converter converter = context.getBean(Converter.class);
                final String str = converter.toString(value);
                return str.getBytes();
            };

            return AwsLambdaAssertions.this;
        }

        @NotNull
        public AwsLambdaAssertions input(@NotNull Function<RuntimeContext, byte[]> value) {
            validateInput();
            this.inputConverter = value;
            return AwsLambdaAssertions.this;
        }
    }

    private final Input input = new Input();
    private final AbstractLambdaEntrypoint entrypoint;

    private AwsLambdaAssertions(AbstractLambdaEntrypoint entrypoint) {
        this.entrypoint = entrypoint;
    }

    @NotNull
    public static Input ofEntrypoint(AbstractLambdaEntrypoint entrypoint) {
        return new AwsLambdaAssertions(entrypoint).input;
    }

    @NotNull
    public String expectString() {
        return inputBytesAndExpectBytes(input.inputConverter, (context, bytes) -> new String(bytes));
    }

    @NotNull
    public InputStream expectStream() {
        return inputBytesAndExpectBytes(input.inputConverter, (context, bytes) -> new ByteArrayInputStream(bytes));
    }

    public byte[] expectBytes() {
        return inputBytesAndExpectBytes(input.inputConverter, (context, bytes) -> bytes);
    }

    @NotNull
    public <T> T expectJson(@NotNull Class<T> expectType) {
        return inputBytesAndExpectBytes(input.inputConverter, (context, bytes) -> {
            final Converter converter = context.getBean(Converter.class);
            final String str = new String(bytes);
            return converter.fromString(str, expectType);
        });
    }

    public void expectThrows() throws Throwable {
        try {
            inputBytesAndExpectBytes(input.inputConverter, (context, bytes) -> bytes);
        } catch (TestingAwsLambdaException e) {
            throw e.getCause();
        }
    }

    private <T> T inputBytesAndExpectBytes(Function<RuntimeContext, byte[]> inputConverter,
                                           BiFunction<RuntimeContext, byte[], T> resultConverter) {
        final TestingEntrypoint testingEntrypoint = new TestingEntrypoint(entrypoint);
        try (final TestingRuntimeContext testingRuntimeContext = (TestingRuntimeContext) testingEntrypoint
                .getRuntimeContext()) {
            testingRuntimeContext.setupInRuntime();

            final byte[] inputAsBytes = inputConverter.apply(testingRuntimeContext);
            final TestingAwsRuntimeClient testingAwsRuntimeClient = testingRuntimeContext.getTestingAwsRuntimeClient();
            testingAwsRuntimeClient.setEvent(new ByteArrayInputStream(inputAsBytes));

            testingEntrypoint.test(new String[0]);

            final Throwable throwable = testingAwsRuntimeClient.getThrowable();
            if (throwable != null) {
                if (throwable instanceof RuntimeException ex) {
                    throw ex;
                } else {
                    throw new TestingAwsLambdaException(throwable);
                }
            } else {
                return resultConverter.apply(testingRuntimeContext, testingAwsRuntimeClient.getResult());
            }
        } catch (Exception e) {
            throw new TestingAwsLambdaException(e);
        } finally {
            try {
                ((TestingRuntimeContext) testingEntrypoint.getRuntimeContext()).closeReal();
            } catch (Exception e) {
                // do nothing
            }
        }
    }
}

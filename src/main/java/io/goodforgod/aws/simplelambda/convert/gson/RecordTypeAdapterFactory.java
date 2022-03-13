package io.goodforgod.aws.simplelambda.convert.gson;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Gson support for Java 16+ record types.
 * <p>
 * Taken from <a href="https://github.com/google/gson/issues/1794">GitHub</a> and adjusted for
 * performance and proper handling of {@link SerializedName} annotations
 *
 * @author Anton Kurako (GoodforGod)
 * @see <a href="https://gist.github.com/knightzmc/cf26d9931d32c78c5d777cc719658639">Github Gist</a>
 * @since 13.03.2022
 */
final class RecordTypeAdapterFactory implements TypeAdapterFactory {

    private static final Map<Class<?>, Object> PRIMITIVE_DEFAULTS = new HashMap<>();

    static {
        PRIMITIVE_DEFAULTS.put(byte.class, (byte) 0);
        PRIMITIVE_DEFAULTS.put(int.class, 0);
        PRIMITIVE_DEFAULTS.put(long.class, 0L);
        PRIMITIVE_DEFAULTS.put(short.class, (short) 0);
        PRIMITIVE_DEFAULTS.put(double.class, 0D);
        PRIMITIVE_DEFAULTS.put(float.class, 0F);
        PRIMITIVE_DEFAULTS.put(char.class, '\0');
        PRIMITIVE_DEFAULTS.put(boolean.class, false);
    }

    private final Map<RecordComponent, List<String>> recordComponentNameCache = new ConcurrentHashMap<>();

    /**
     * Get all names of a record component
     * If annotated with {@link SerializedName} the list returned will be the primary name first, then
     * any alternative names
     * Otherwise, the component name will be returned.
     */
    private List<String> getRecordComponentNames(final RecordComponent recordComponent) {
        final List<String> inCache = recordComponentNameCache.get(recordComponent);
        if (inCache != null) {
            return inCache;
        }

        final List<String> names = new ArrayList<>(4);
        // The @SerializedName is compiled to be part of the componentName() method
        // The use of a loop is also deliberate, getAnnotation seemed to return null if Gson's package was
        // relocated
        SerializedName annotation = null;
        for (Annotation a : recordComponent.getAccessor().getAnnotations()) {
            if (a.annotationType() == SerializedName.class) {
                annotation = (SerializedName) a;
                break;
            }
        }

        if (annotation != null) {
            names.add(annotation.value());
            names.addAll(Arrays.asList(annotation.alternate()));
        } else {
            names.add(recordComponent.getName());
        }

        final List<String> namesList = List.copyOf(names);
        recordComponentNameCache.put(recordComponent, namesList);
        return namesList;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final Class<T> clazz = (Class<T>) type.getRawType();
        if (!clazz.isRecord()) {
            return null;
        }

        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return new TypeAdapter<>() {

            @Override
            public void write(JsonWriter out, T value) throws IOException {
                delegate.write(out, value);
            }

            @Override
            public T read(JsonReader reader) throws IOException {
                if (reader.peek() == JsonToken.NULL) {
                    reader.nextNull();
                    return null;
                }

                var recordComponents = clazz.getRecordComponents();
                var typeMap = new HashMap<String, TypeToken<?>>();
                for (RecordComponent recordComponent : recordComponents) {
                    for (String name : getRecordComponentNames(recordComponent)) {
                        typeMap.put(name, TypeToken.get(recordComponent.getGenericType()));
                    }
                }

                final Map<String, Object> argsMap = new HashMap<>(6);
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    var type = typeMap.get(name);
                    if (type != null) {
                        argsMap.put(name, gson.getAdapter(type).read(reader));
                    } else {
                        gson.getAdapter(Object.class).read(reader);
                    }
                }
                reader.endObject();

                var argTypes = new Class<?>[recordComponents.length];
                var args = new Object[recordComponents.length];
                for (int i = 0; i < recordComponents.length; i++) {
                    argTypes[i] = recordComponents[i].getType();
                    List<String> names = getRecordComponentNames(recordComponents[i]);
                    Object value = null;
                    TypeToken<?> type = null;
                    // Find the first matching type and value
                    for (String name : names) {
                        value = argsMap.get(name);
                        type = typeMap.get(name);
                        if (value != null && type != null) {
                            break;
                        }
                    }

                    if (value == null && (type != null && type.getRawType().isPrimitive())) {
                        value = PRIMITIVE_DEFAULTS.get(type.getRawType());
                    }
                    args[i] = value;
                }

                try {
                    final Constructor<T> constructor = clazz.getDeclaredConstructor(argTypes);
                    constructor.setAccessible(true);
                    return constructor.newInstance(args);
                } catch (NoSuchMethodException | InstantiationException | SecurityException | IllegalAccessException
                        | IllegalArgumentException | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            }
        };
    }
}

package io.goodforgod.aws.simplelambda.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Copycat of GenericTypeUtils from Micronaut.
 *
 * @author Anton Kurako (GoodforGod)
 * @since 14.08.2021
 */
public class ReflectionUtils {

    public static final Class[] EMPTY_CLASS_ARRAY = new Class[0];

    private ReflectionUtils() {}

    /**
     * @param aClass     Some class
     * @param interfaces The interfaces
     * @return A set of interfaces
     */
    private static Set<Type> populateInterfaces(Class<?> aClass, Set<Type> interfaces) {
        Type[] theInterfaces = aClass.getGenericInterfaces();
        interfaces.addAll(Arrays.asList(theInterfaces));
        for (Type theInterface : theInterfaces) {
            if (theInterface instanceof Class) {
                Class<?> i = (Class<?>) theInterface;
                Type[] genericInterfaces = i.getGenericInterfaces();
                if (genericInterfaces.length > 0) {
                    populateInterfaces(i, interfaces);
                }
            }
        }

        if (!aClass.isInterface()) {
            Class<?> superclass = aClass.getSuperclass();
            while (superclass != null) {
                populateInterfaces(superclass, interfaces);
                superclass = superclass.getSuperclass();
            }
        }

        return interfaces;
    }

    /**
     * @param aClass A class
     * @return All generic interfaces
     */
    private static Set<Type> getAllGenericInterfaces(Class<?> aClass) {
        Set<Type> interfaces = new LinkedHashSet<>();
        return populateInterfaces(aClass, interfaces);
    }

    /**
     * Resolve all of the type arguments for the given interface from the given
     * type. Also searches superclasses.
     *
     * @param type          The type to resolve from
     * @param interfaceType The interface to resolve from
     * @return The type arguments to the interface
     */
    public static Class[] resolveInterfaceTypeArguments(Class<?> type, Class<?> interfaceType) {
        Optional<Type> resolvedType = getAllGenericInterfaces(type).stream()
                .filter(t -> {
                    if (t instanceof ParameterizedType) {
                        ParameterizedType pt = (ParameterizedType) t;
                        return pt.getRawType() == interfaceType;
                    }
                    return false;
                })
                .findFirst();

        return resolvedType.map(ReflectionUtils::resolveTypeArguments)
                .orElse(EMPTY_CLASS_ARRAY);
    }

    /**
     * Resolves the type arguments for a generic type.
     *
     * @param genericType The generic type
     * @return The type arguments
     */
    public static Class[] resolveTypeArguments(Type genericType) {
        Class[] typeArguments = EMPTY_CLASS_ARRAY;
        if (genericType instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) genericType;
            typeArguments = resolveParameterizedType(pt);
        }
        return typeArguments;
    }

    private static Class[] resolveParameterizedType(ParameterizedType pt) {
        Class[] typeArguments = EMPTY_CLASS_ARRAY;
        Type[] actualTypeArguments = pt.getActualTypeArguments();
        if (actualTypeArguments != null && actualTypeArguments.length > 0) {
            typeArguments = new Class[actualTypeArguments.length];
            for (int i = 0; i < actualTypeArguments.length; i++) {
                Type actualTypeArgument = actualTypeArguments[i];
                Optional<Class> opt = resolveParameterizedTypeArgument(actualTypeArgument);
                if (opt.isPresent()) {
                    typeArguments[i] = opt.get();
                } else {
                    typeArguments = EMPTY_CLASS_ARRAY;
                    break;
                }
            }
        }
        return typeArguments;
    }

    /**
     * @param actualTypeArgument The actual type argument
     * @return An optional with the resolved parameterized class
     */
    private static Optional<Class> resolveParameterizedTypeArgument(Type actualTypeArgument) {
        if (actualTypeArgument instanceof Class) {
            return Optional.of((Class) actualTypeArgument);
        }
        if (actualTypeArgument instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) actualTypeArgument;
            Type rawType = pt.getRawType();
            if (rawType instanceof Class) {
                return Optional.of((Class) rawType);
            }
        }
        return Optional.empty();
    }
}

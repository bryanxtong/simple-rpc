package com.bryan.rpc.common.utils;

import java.lang.reflect.*;
import java.util.concurrent.CompletableFuture;

public class TypeUtils {

    public static Class<?> extractRawClass(Type type) {
        if (type == null) {
            return null;
        }
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            Type rawType = ((ParameterizedType) type).getRawType();
            return extractRawClass(rawType);
        }
        if (type instanceof TypeVariable<?> typeVar) {
            Type[] bounds = typeVar.getBounds();
            if (bounds.length > 0) {
                return extractRawClass(bounds[0]);
            }
            return Object.class;
        }
        if (type instanceof WildcardType wildcardType) {
            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length > 0) {
                return extractRawClass(upperBounds[0]);
            }
            Type[] lowerBounds = wildcardType.getLowerBounds();
            if (lowerBounds.length > 0) {
                return extractRawClass(lowerBounds[0]);
            }
            return Object.class;
        }

        if (type instanceof GenericArrayType arrayType) {
            Type componentType = arrayType.getGenericComponentType();

            Class<?> componentClass = extractRawClass(componentType);
            if (componentClass != null) {
                return java.lang.reflect.Array.newInstance(componentClass, 0).getClass();
            }

            return Object[].class;
        }
        return null;
    }

    public static Type extractWrappedType(Type futureType) {
        ParameterizedType parameterizedType = (ParameterizedType) futureType;
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments.length != 1) {
            throw new IllegalStateException("wrong number of actual type arguments");
        }
        return actualTypeArguments[0];
    }
}

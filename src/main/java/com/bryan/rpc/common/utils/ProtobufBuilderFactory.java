package com.bryan.rpc.common.utils;

import com.google.protobuf.GeneratedMessage;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class ProtobufBuilderFactory {
    public static GeneratedMessage.Builder<?> createBuilder(Type protobufType) throws Exception {
        Class<?> protobufClass;

        if (protobufType instanceof Class) {
            protobufClass = (Class<?>) protobufType;
        } else if (protobufType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) protobufType;
            Type rawType = parameterizedType.getRawType();
            if (rawType instanceof Class) {
                protobufClass = (Class<?>) rawType;
            } else {
                throw new IllegalArgumentException("cannot handle the raw type: " + rawType);
            }
        } else {
            throw new IllegalArgumentException("Unsupported Type: " + protobufType.getClass());
        }
        if (protobufClass == null) {
            throw new IllegalArgumentException("could not find class from the type: " + protobufType);
        }
        Method newBuilderMethod = protobufClass.getMethod("newBuilder");
        Object builderObj = newBuilderMethod.invoke(null);
        @SuppressWarnings("unchecked")
        GeneratedMessage.Builder<?> builder = (GeneratedMessage.Builder<?>) builderObj;
        return builder;
    }
}
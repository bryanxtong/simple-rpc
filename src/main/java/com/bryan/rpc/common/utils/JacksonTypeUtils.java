package com.bryan.rpc.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class JacksonTypeUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public static Object[] deserializeMethodParams(Object[] rawParams, Method method) {
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        if(genericParameterTypes ==null || genericParameterTypes.length != rawParams.length) {
            throw new IllegalArgumentException("params cannot match or could get parameter types information.");
        }

        Object[] deserializedParams = new Object[rawParams.length];
        for(int i = 0;i< rawParams.length;i++) {
            Object rawParam = rawParams[i];
            Type paramType = genericParameterTypes[i]; //ParameterizedType
            deserializedParams[i] = convertValue(rawParam, paramType);
        }
        return deserializedParams;

    }
    public static <T> T convertValue(Object fromValue,Class<T> clazz){
        return objectMapper.convertValue(fromValue,clazz);
    }

    public static <T> T fromJson(String json, TypeReference<T> typeRef){
        try {
            return objectMapper.readValue(json,typeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromByteArray(byte[] bytes, Type type){
        JavaType javaType = objectMapper.getTypeFactory().constructType(type);
        try {
            return objectMapper.readValue(bytes,javaType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Type type){
        JavaType javaType = objectMapper.getTypeFactory().constructType(type);
        try {
            return objectMapper.readValue(json,javaType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> typeRef){
        return objectMapper.convertValue(fromValue,typeRef);
    }

    public static Object convertValue(Object value, Type targetType){
        if(value == null){
            return null;
        }
        if(targetType instanceof Class){
            Class<?> targetClass = (Class<?>)targetType;
            if(targetClass.isInstance(value)){
                return value;
            }
        }
        JavaType javaType = objectMapper.getTypeFactory().constructType(targetType);
        return objectMapper.convertValue(value,javaType);
    }
}

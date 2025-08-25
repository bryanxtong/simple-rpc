package com.bryan.rpc.common.serializer;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.lang.reflect.Type;

public class JacksonSerializer implements Serializer {
    private static final JacksonSerializer instance = new JacksonSerializer();

    public static JacksonSerializer getInstance() {
        return instance;
    }

    /**
     * For java 8 time library, use ISO-8601 string, otherwise for below case, It will evaluate to Double
     * Object[] array = new Object[]{Instant.now()};
     * byte[] json = objectMapper.writeValueAsBytes(array);
     * Object[] objects = objectMapper.readValue(json, Object[].class);
     * System.out.println(objects[0]);
     */
    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    public <T> byte[] serialize(T obj) {
        try {
            return objectMapper.writeValueAsBytes(obj);
        } catch (IOException e) {
            throw new RuntimeException("serialization failure: " + e.getMessage(), e);
        }
    }

    public <T> T deserialize(byte[] bytes, Type type) {
        JavaType javaType = objectMapper.getTypeFactory().constructType(type);
        try {
            return objectMapper.readValue(bytes, javaType);
        } catch (IOException e) {
            throw new RuntimeException("deserialization failure: " + e.getMessage(), e);
        }
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            throw new RuntimeException("deserialization failure: " + e.getMessage(), e);
        }
    }
}
